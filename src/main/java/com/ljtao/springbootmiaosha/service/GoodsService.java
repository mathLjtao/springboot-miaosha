package com.ljtao.springbootmiaosha.service;

import com.ljtao.springbootmiaosha.dao.GoodsMapper;
import com.ljtao.springbootmiaosha.dao.MiaoshaGoodsMapper;
import com.ljtao.springbootmiaosha.dao.MiaoshaOrderMapper;
import com.ljtao.springbootmiaosha.dao.OrderInfoMapper;
import com.ljtao.springbootmiaosha.model.Goods;
import com.ljtao.springbootmiaosha.model.MiaoshaOrder;
import com.ljtao.springbootmiaosha.model.OrderInfo;
import com.ljtao.springbootmiaosha.model.User;
import com.ljtao.springbootmiaosha.redis.RedisService;
import com.ljtao.springbootmiaosha.util.CodeMsg;
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
    @Autowired
    private RedisService redisService;
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
    public OrderInfo handleMiaoshaOrder(User user, GoodsVo goods) throws Exception{
        //减库存，下订单，下秒杀订单
        int i = miaoshaGoodsMapper.MiaoshaReduceOne(goods.getId());
        if(i<1){
            throw new Exception("无法减少秒杀商品库存，"+CodeMsg.MIAOSHA_OVAE.getMsg());
        }
        OrderInfo orderInfo=OrderInfo.builder().createDate(new Date()).deliveryAddrId(0l).goodsCount(1)
                .goodsName(goods.getGoodsName()).goodsPrice(goods.getMiaoshaPrice()).orderChannel((byte)1)
                .status((byte)0).userId(user.getId()).goodId(goods.getId()).build();
        orderInfoMapper.insertSelective(orderInfo);
        MiaoshaOrder miaoshaOrder=new MiaoshaOrder();
        miaoshaOrder.setGoodId(goods.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());
        miaoshaOrderMapper.insertSelective(miaoshaOrder);
        return orderInfo;
    }
    @Transactional
    public OrderInfo optimizeHandleMiaosha(User user, GoodsVo goods) throws Exception{
        //减库存，下订单，下秒杀订单
        int i = miaoshaGoodsMapper.MiaoshaReduceOne(goods.getId());
        if(i<1){
            //这里说明库存没有了，顺便将此个商品没有库存的信息保存在redis中
            redisService.setGoodsOver(goods.getId());
            throw new Exception("无法减少秒杀商品库存，"+CodeMsg.MIAOSHA_OVAE.getMsg());
        }
        OrderInfo orderInfo=OrderInfo.builder().createDate(new Date()).deliveryAddrId(0l).goodsCount(1)
                .goodsName(goods.getGoodsName()).goodsPrice(goods.getMiaoshaPrice()).orderChannel((byte)1)
                .status((byte)0).userId(user.getId()).goodId(goods.getId()).build();
        orderInfoMapper.insertSelective(orderInfo);
        MiaoshaOrder miaoshaOrder=new MiaoshaOrder();
        miaoshaOrder.setGoodId(goods.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());
        miaoshaOrderMapper.insertSelective(miaoshaOrder);
        return orderInfo;
    }
}
