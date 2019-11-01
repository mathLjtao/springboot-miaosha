package com.ljtao.springbootmiaosha.service;

import com.ljtao.springbootmiaosha.dao.GoodsMapper;
import com.ljtao.springbootmiaosha.dao.MiaoshaGoodsMapper;
import com.ljtao.springbootmiaosha.dao.MiaoshaOrderMapper;
import com.ljtao.springbootmiaosha.dao.OrderInfoMapper;
import com.ljtao.springbootmiaosha.model.Goods;
import com.ljtao.springbootmiaosha.model.MiaoshaOrder;
import com.ljtao.springbootmiaosha.model.OrderInfo;
import com.ljtao.springbootmiaosha.model.User;
import com.ljtao.springbootmiaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private MiaoshaOrderMapper miaoshaOrderMapper;
    @Autowired
    private MiaoshaGoodsMapper miaoshaGoodsMapper;
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    public List<Goods> getAll(){
        List<Goods> all = goodsMapper.getAll();
        return all;
    }

    public List<GoodsVo> getAllMiaoshaGoods() {
        List<GoodsVo> list=goodsMapper.getAllMiaoshaGoods();
        return list;

    }

    public GoodsVo getMiaoshaGoodsById(Long goodsId) {
        GoodsVo goods=goodsMapper.getMiaoshaGoodsById(goodsId);
        return goods;
    }
    @Transactional
    public OrderInfo handleMiaoshaOrder(User user, GoodsVo goods) {
        //减库存，下订单，下秒杀订单
        miaoshaGoodsMapper.MiaoshaReduceOne(goods.getId());
        OrderInfo orderInfo=OrderInfo.builder().createDate(new Date()).deliveryAddrId(0l).goodsCount(1)
                .goodsName(goods.getGoodsName()).goodsPrice(goods.getMiaoshaPrice()).orderChannel((byte)1)
                .status((byte)0).userId(user.getId()).goodId(goods.getId()).build();
        orderInfoMapper.insertSelective(orderInfo);
        MiaoshaOrder miaoshaOrder=new MiaoshaOrder();
        miaoshaOrder.setGoodId(goods.getId());
        miaoshaOrder.setOderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());
        miaoshaOrderMapper.insertSelective(miaoshaOrder);
        return orderInfo;
    }
}
