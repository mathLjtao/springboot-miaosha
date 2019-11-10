package com.ljtao.springbootmiaosha.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
@Service
public class RedisService<K,V> {
    @Autowired
    private RedisTemplate redisTemplate;
    public static final String GOODS_STOCK_PRE="GOODS_STOCK";
    public  void set(K key, V value, long time){
        ValueOperations<K,V> operations = redisTemplate.opsForValue();
        operations.set(key,value,time,TimeUnit.SECONDS);
    }
    public V get(K key){
        ValueOperations<K,V> operations = redisTemplate.opsForValue();

        return operations.get(key);
    }
    public void setGoodsOver(Long goodsId) {
        redisTemplate.opsForValue().set("isGoodsOver", true, 3600,TimeUnit.SECONDS);
    }

    public boolean getGoodsOver(long goodsId) {
        if(redisTemplate.opsForValue().get("isGoodsOver")==null)
            return false;
        return (boolean)redisTemplate.opsForValue().get("isGoodsOver");
    }
}
