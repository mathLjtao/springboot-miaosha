package com.ljtao.springbootmiaosha.dao;


import com.ljtao.springbootmiaosha.model.MiaoshaGoods;

public interface MiaoshaGoodsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MiaoshaGoods record);

    int insertSelective(MiaoshaGoods record);

    MiaoshaGoods selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MiaoshaGoods record);

    int updateByPrimaryKey(MiaoshaGoods record);

    //根据goodsId，减少当前获取的一个数量
    int MiaoshaReduceOne(Long goodsId);
}