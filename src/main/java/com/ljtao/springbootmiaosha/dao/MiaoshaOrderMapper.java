package com.ljtao.springbootmiaosha.dao;


import com.ljtao.springbootmiaosha.model.MiaoshaOrder;
import org.apache.ibatis.annotations.Param;

public interface MiaoshaOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MiaoshaOrder record);

    int insertSelective(MiaoshaOrder record);

    MiaoshaOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MiaoshaOrder record);

    int updateByPrimaryKey(MiaoshaOrder record);

    int countByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);

    MiaoshaOrder selectByUserIdAndGoodsId(@Param("userId") Long userId,@Param("goodsId") Long goodsId);
}