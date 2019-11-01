package com.ljtao.springbootmiaosha.dao;


import com.ljtao.springbootmiaosha.model.Goods;
import com.ljtao.springbootmiaosha.vo.GoodsVo;

import java.util.List;

public interface GoodsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Goods record);

    int insertSelective(Goods record);

    Goods selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Goods record);

    int updateByPrimaryKeyWithBLOBs(Goods record);

    int updateByPrimaryKey(Goods record);

    List<Goods>  getAll();

    List<GoodsVo> getAllMiaoshaGoods();

    GoodsVo getMiaoshaGoodsById(Long goodsId);
}