package com.ljtao.springbootmiaosha.controller;

import com.ljtao.springbootmiaosha.access.AccessLimit;
import com.ljtao.springbootmiaosha.domian.MiaoshaMessage;
import com.ljtao.springbootmiaosha.model.MiaoshaOrder;
import com.ljtao.springbootmiaosha.model.OrderInfo;
import com.ljtao.springbootmiaosha.model.User;
import com.ljtao.springbootmiaosha.rabbitmq.MQSender;
import com.ljtao.springbootmiaosha.redis.RedisService;
import com.ljtao.springbootmiaosha.service.*;
import com.ljtao.springbootmiaosha.util.CodeMsg;
import com.ljtao.springbootmiaosha.util.JsonData;
import com.ljtao.springbootmiaosha.util.UUIDUtil;
import com.ljtao.springbootmiaosha.vo.GoodsVo;
import com.ljtao.springbootmiaosha.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/miaosha/miaosha")
public class MiaoshaController {
    @Autowired
    private UserService userService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private MiaoshaOrderService miaoshaOrderService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MiaoshaService miaoshaService;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisTemplate redisTemplate;

    //判断缓存中的库存是否已经全部没了，可以减少访问缓存
    private Map<Long,Boolean> localOverMap =new HashMap<Long, Boolean>();

    /*
        通过货物id，进行减库存，下订单，下秒杀订单
     */
    @RequestMapping("do_miaosha")
    public String doMiaosha(HttpServletResponse res, HttpServletRequest req
            , @RequestParam("goodsId") Long goodsId, Model model) throws Exception {
        User user = userService.getUserByRequest(req, res);
        if(user==null){
            return "login";
        }
        GoodsVo goods=goodsService.getMiaoshaGoodsById(goodsId);
        //判断库存
        if(goods.getStockCount()<1){
            model.addAttribute("errmsg",CodeMsg.MIAOSHA_OVAE.getMsg());
            return "miaosha_fail";
        }
        //检查是否有重复秒杀
        int checkOrderNum=miaoshaOrderService.countByUserIdAndGoodsId(user.getId(),goodsId);
        if(checkOrderNum>0){
            model.addAttribute("errmsg", CodeMsg.MIAOSHA_REPEATE.getMsg());
            return "miaosha_fail";
        }
        OrderInfo orderInfo=goodsService.handleMiaoshaOrder(user,goods);


        model.addAttribute("goods",goods);
        model.addAttribute("orderInfo",orderInfo);
        return "order_detail";
    }
    /*
        将秒杀后转向，是静态页面来接收数据
     */
    @RequestMapping("static_do_miaosha")
    @ResponseBody
    public JsonData staticDoMiaosha(HttpServletResponse res, HttpServletRequest req
            , @RequestParam("goodsId") Long goodsId) throws Exception {
        User user = userService.getUserByRequest(req, res);
        if(user==null){
            return JsonData.fail("用户未登录，请先登录！");
        }
        GoodsVo goods=goodsService.getMiaoshaGoodsById(goodsId);
        //判断库存
        if(goods.getStockCount()<1){
           return JsonData.fail(CodeMsg.MIAOSHA_OVAE.getMsg());
        }
        //检查是否有重复秒杀
        int checkOrderNum=miaoshaOrderService.countByUserIdAndGoodsId(user.getId(),goodsId);
        if(checkOrderNum>0){
            return JsonData.fail(CodeMsg.MIAOSHA_REPEATE.getMsg());
        }
        OrderInfo orderInfo=goodsService.handleMiaoshaOrder(user,goods);


        return JsonData.success(orderInfo);
    }
    /*
        运用redis、rabbitmq来处理商品秒杀  ，goods_detail_optimize.htm 页面调用
     */
    @RequestMapping("optimizeHandleMiaosha")
    @ResponseBody
    public JsonData optimizeHandleMiaosha(HttpServletResponse res, HttpServletRequest req
            , @RequestParam("goodsId") Long goodsId){
        User user = userService.getUserByRequest(req, res);
        if(user==null){
            return JsonData.fail("用户未登录，请先登录！");
        }
        //内存标记，减少redis的访问
        Boolean over = localOverMap.get(goodsId);
        if( over!=null && over){
            return JsonData.fail("商品已被秒杀完毕！");
        }
        Object o = redisService.get(RedisService.GOODS_STOCK_PRE + goodsId);
        Integer stockCount;
        //判断是否将秒杀商品的库存数量加入到redis中
        if(o==null){
            GoodsVo goods = goodsService.getMiaoshaGoodsById(goodsId);
            stockCount = goods.getStockCount();
            redisService.set(RedisService.GOODS_STOCK_PRE +goodsId,stockCount,3600);
        }
        else{
            stockCount=(Integer)o;
        }
        if(stockCount<1){
            localOverMap.put(goodsId,true);
            return JsonData.fail("商品已被秒杀完毕！");
        }
        //检查是否有重复秒杀
        int checkOrderNum=miaoshaOrderService.countByUserIdAndGoodsId(user.getId(),goodsId);
        if(checkOrderNum>0){
            return JsonData.fail(CodeMsg.MIAOSHA_REPEATE.getMsg());
        }
        redisService.set(RedisService.GOODS_STOCK_PRE +goodsId,stockCount-1,3600);
        MiaoshaMessage miaoshaMessage = new MiaoshaMessage();
        miaoshaMessage.setGoodsId(goodsId);
        miaoshaMessage.setUser(user);
        mqSender.miaoshaSend(JsonData.beanToString(miaoshaMessage));

        return JsonData.success("排队中",0);
    }
    /**
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     * */
    @RequestMapping("optimizeHandleMiaoshaResult")
    @ResponseBody
    public JsonData optimizeHandleMiaoshaResult(HttpServletResponse res, HttpServletRequest req
            , @RequestParam("goodsId") Long goodsId){
        User user = userService.getUserByRequest(req, res);
        if(user==null){
            return JsonData.fail("用户未登录，请先登录！");
        }
        MiaoshaOrder miaoshaOrder=miaoshaOrderService.selectByUserIdAndGoodsId(user.getId(),goodsId);
        if(miaoshaOrder!=null){
            return JsonData.success("秒杀成功！",miaoshaOrder.getOrderId());
        }
        else{

            if(redisService.getGoodsOver(goodsId)){
                return JsonData.success(-1);
            }
            else{
                return JsonData.success(0);
            }
        }
    }

    /*
        1、生成变化的访问接口。（隐藏秒杀接口）
        2、验证码判断。
        3、接口限流防刷。
     */
    @AccessLimit(seconds = 5,maxCount = 3)//间隔seconds秒内最多只能访问maxCount次，超过限制后需再等seconds秒才能访问，自己实现的注解配置，这个需要掌握起来
    @RequestMapping("/path")//生成随机秒杀接口参数
    @ResponseBody
    public JsonData getMiaoshaPath(HttpServletResponse res, HttpServletRequest req,User user
            , @RequestParam("goodsId") Long goodsId
            ,@RequestParam(value="verifyCode",defaultValue = "0") int verifyCode) throws Exception {
        if(user==null){
            return JsonData.fail("用户未登录，请先登录！");
        }
        //检验验证码
        boolean check = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
        if(!check){
            return JsonData.fail(CodeMsg.REQUEST_ILLEGAL.getMsg()+",验证码错误！");
        }
        String path=miaoshaService.createMiaoshaPath(user,goodsId);
        return JsonData.success(path);
    }
    @RequestMapping("/{path}/dynamicAPIDoMiaosha")
    @ResponseBody
    public JsonData dynamicAPIDoMiaosha(HttpServletResponse res, HttpServletRequest req
            , @RequestParam("goodsId") Long goodsId, @PathVariable("path") String path) throws Exception{
        User user = userService.getUserByRequest(req, res);
        if(user==null){
            return JsonData.fail("用户未登录，请先登录！");
        }
        boolean check=miaoshaService.checkMiaoshaPath(user, goodsId, path);
        if(!check){
            return JsonData.fail(CodeMsg.REQUEST_ILLEGAL.getMsg());
        }
        GoodsVo goods=goodsService.getMiaoshaGoodsById(goodsId);
        //判断库存
        if(goods.getStockCount()<1){
            return JsonData.fail(CodeMsg.MIAOSHA_OVAE.getMsg());
        }
        //检查是否有重复秒杀
        int checkOrderNum=miaoshaOrderService.countByUserIdAndGoodsId(user.getId(),goodsId);
        if(checkOrderNum>0){
            return JsonData.fail(CodeMsg.MIAOSHA_REPEATE.getMsg());
        }
        OrderInfo orderInfo=goodsService.handleMiaoshaOrder(user,goods);
        return JsonData.success(orderInfo.getId());
    }
    /**
     * orderId：成功
     * -1：秒杀失败
     * */
    @RequestMapping("/dynamicAPIDoMiaoshaResult")
    @ResponseBody
    public JsonData dynamicAPIDoMiaoshaResult(HttpServletResponse res, HttpServletRequest req
            , @RequestParam("goodsId") Long goodsId){
        User user = userService.getUserByRequest(req, res);
        if(user==null){
            return JsonData.fail("用户未登录，请先登录！");
        }
        MiaoshaOrder miaoshaOrder=miaoshaOrderService.selectByUserIdAndGoodsId(user.getId(),goodsId);
        if(miaoshaOrder!=null){
            return JsonData.success("秒杀成功！",miaoshaOrder.getOrderId());
        }
        else{
            return JsonData.success(-1);

        }
    }
    @RequestMapping("/verifyCode")
    @ResponseBody
    public JsonData getMiaoshaVerifyCode(HttpServletResponse res, HttpServletRequest req
            , @RequestParam("goodsId") Long goodsId){
        User user = userService.getUserByRequest(req, res);
        if(user==null){
            return JsonData.fail("用户未登录，请先登录！");
        }
        try {
            BufferedImage image  = miaoshaService.createVerifyCode(user, goodsId);
            OutputStream out = res.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        }catch(Exception e) {
            e.printStackTrace();
            return JsonData.fail(CodeMsg.MIAOSHA_FAIL.getMsg());
        }
    }

}
