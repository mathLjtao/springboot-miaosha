package com.ljtao.springbootmiaosha.controller;

import com.ljtao.springbootmiaosha.model.OrderInfo;
import com.ljtao.springbootmiaosha.model.User;
import com.ljtao.springbootmiaosha.service.GoodsService;
import com.ljtao.springbootmiaosha.service.MiaoshaOrderService;
import com.ljtao.springbootmiaosha.service.UserService;
import com.ljtao.springbootmiaosha.util.CodeMsg;
import com.ljtao.springbootmiaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/miaosha/miaosha")
public class MiaoshaController {
    @Autowired
    private UserService userService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private MiaoshaOrderService miaoshaOrderService;

    /*
        通过货物id，进行减库存，下订单，下秒杀订单
     */
    @RequestMapping("do_miaosha")
    public String doMiaosha(HttpServletResponse res, HttpServletRequest req
            , @RequestParam("goodsId") Long goodsId, Model model){
        User user = userService.getUserByRequest(req, res);
        if(user==null){
            return "login";
        }
        //检查是否有重复秒杀
        int checkOrderNum=miaoshaOrderService.selectByUserIdAndGoodsId(user.getId(),goodsId);
        if(checkOrderNum>0){
            model.addAttribute("errmsg", CodeMsg.MIAOSHA_REPEATE.getMsg());
            return "miaosha_fail";
        }
        GoodsVo goods=goodsService.getMiaoshaGoodsById(goodsId);
        OrderInfo orderInfo=goodsService.handleMiaoshaOrder(user,goods);


        model.addAttribute("goods",goods);
        model.addAttribute("orderInfo",orderInfo);
        return "order_detail";
    }
}
