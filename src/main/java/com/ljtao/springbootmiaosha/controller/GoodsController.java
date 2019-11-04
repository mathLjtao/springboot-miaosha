package com.ljtao.springbootmiaosha.controller;

import com.ljtao.springbootmiaosha.model.Goods;
import com.ljtao.springbootmiaosha.model.User;
import com.ljtao.springbootmiaosha.redis.RedisService;
import com.ljtao.springbootmiaosha.service.GoodsService;
import com.ljtao.springbootmiaosha.service.UserService;
import com.ljtao.springbootmiaosha.util.JsonData;
import com.ljtao.springbootmiaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/miaosha/goods")
public class GoodsController {
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserService userService;
    @Autowired
    private GoodsService goodsService;
    /*
        获取所有货品信息
     */
    @RequestMapping("all_goods")
    @ResponseBody
    public JsonData getAllGoods(){
        List<Goods> all = goodsService.getAll();

        return JsonData.success(all);
    }

    /*
        所有商品列表,其中包含秒杀的信息
     */
    @RequestMapping("all_miaosha_goods")
    public String getAllMiaoshaGoods(Model model){
        List<GoodsVo> list=goodsService.getAllMiaoshaGoods();
        model.addAttribute("goodsList",list);
        return "goods_list";
    }
    /*
        根据id显示秒杀商品的详情
     */
    @RequestMapping("to_miaosha_detail/{goodsId}")
    public String getMiaoshaGoodsById(Model model, HttpServletRequest req, HttpServletResponse res,
                               @PathVariable Long goodsId){
        User user =userService.getUserByRequest(req,res);
        model.addAttribute("user",user);
        GoodsVo goods=goodsService.getMiaoshaGoodsById(goodsId);
        model.addAttribute("goods",goods);
        int miaoshaStatus=0;
        int remainSeconds=0;

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now =System.currentTimeMillis();
        if(now-startAt<0){
            miaoshaStatus=0;
            remainSeconds=(int)((startAt-now)/1000);
        }else if(now-endAt>0){
            remainSeconds=-1;
            miaoshaStatus=-1;
        }
        else{
            miaoshaStatus=1;
        }
        model.addAttribute("startAt",startAt);
        model.addAttribute("endAt",endAt);
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        return "goods_detail";
    }

    /*
    测试登录后跳转的情况
     */
    @RequestMapping("to_list")
    public String toList(@CookieValue(value="userToken" ,required=false ) String springCookieToken, HttpServletRequest req , HttpServletResponse res, Model model){
        //获取cookie中信息
        Cookie[] cookies = req.getCookies();
        String cookieToken="";
        if(cookies==null||cookies.length<1){
            return "login";
        }
        for(Cookie c:cookies){
            if("userToken".equals(c.getName())){
                cookieToken=c.getValue();
            }
        }
        System.out.println(cookieToken);
        if(cookieToken!=null && !"".equals(cookieToken)){
            //从redis中根据cookie获取用户信息
            User userByToken=(User)redisService.get(cookieToken);
            //判断用户是否在缓存还存在
            if(userByToken!=null){
                userService.addCookie(res,cookieToken,userByToken);//延长cookie的有效期
                model.addAttribute("user",userByToken);
                return "goods_list";
            }
        }
        return "login";

    }
    @RequestMapping("test_to_list")
    public String toListTest(Model model){
        User user=new User();
        user.setName("hahah");
        model.addAttribute("user",user);
        return "goods_list";
    }
}
