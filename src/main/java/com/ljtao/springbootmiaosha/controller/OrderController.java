package com.ljtao.springbootmiaosha.controller;

import com.ljtao.springbootmiaosha.model.OrderInfo;
import com.ljtao.springbootmiaosha.service.GoodsService;
import com.ljtao.springbootmiaosha.service.OrderInfoService;
import com.ljtao.springbootmiaosha.util.JsonData;
import com.ljtao.springbootmiaosha.vo.GoodsVo;
import com.ljtao.springbootmiaosha.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
@RequestMapping("/miaosha/order")
public class OrderController {
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private GoodsService goodsService;
    @RequestMapping("static_getOrderById")
    @ResponseBody
    public JsonData staticGetOrderByOrderId (@RequestParam("orderId")  String orderId){
        OrderDetailVo odv=new OrderDetailVo();
        OrderInfo orderById = orderInfoService.getOrderById(Long.parseLong(orderId));
        odv.setOrder(orderById);
        GoodsVo miaoshaGoodsById = goodsService.getMiaoshaGoodsById(orderById.getGoodId());
        odv.setGoods(miaoshaGoodsById);
        return JsonData.success(odv);
    }
}
