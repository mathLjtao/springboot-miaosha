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
    public static final String GOODS_STOCK_PRE="GOODS_STOCK";//存储秒杀商品库存的前缀
    public static final String MIAOSHA_PATH="miaosha_path";//生成秒杀接口的随机参数前缀
    public  static final String MiaoshaVerifyCode="vc";//秒杀接口验证码的前缀
    public static final String ACCESS_LIMIT="AccessLimit";//接口访问前缀
    public  void set(K key, V value, long time){
        ValueOperations<K,V> operations = redisTemplate.opsForValue();
        operations.set(key,value,time,TimeUnit.SECONDS);
    }
    public  void set(K key, V value){
        //这样设置的话会让缓存一直存在
        ValueOperations<K,V> operations = redisTemplate.opsForValue();
        operations.set(key,value);
    }
    public void setAndSerializer(){
        //ValueOperations operations = redisTemplate.opsForValue();
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
    public Boolean delete(Object obj){
        return redisTemplate.delete(obj);
    }
    /*
    //向redis里存入数据和设置缓存时间  
    redisTemplate.opsForValue().set("baike", "100", 60 * 10, TimeUnit.SECONDS);
    //val做-1操作  
    redisTemplate.boundValueOps("baike").increment(-1);
    //根据key获取缓存中的val  
    redisTemplate.opsForValue().get("baike")
    //val +1  
    redisTemplate.boundValueOps("baike").increment(1);
    //根据key获取过期时间  
    redisTemplate.getExpire("baike");
    //根据key获取过期时间并换算成指定单位  
    redisTemplate.getExpire("baike",TimeUnit.SECONDS);
    //根据key删除缓存  
    redisTemplate.delete("baike");
    //检查key是否存在，返回boolean值  
    redisTemplate.hasKey("baike");
    //向指定key中存放set集合  
    redisTemplate.opsForSet().add("baike", "1","2","3");
    //设置过期时间  
    redisTemplate.expire("baike",1000 , TimeUnit.MILLISECONDS);
    //根据key查看集合中是否存在指定数据  
    redisTemplate.opsForSet().isMember("baike", "1");
    //根据key获取set集合
    redisTemplate.opsForSet().members("baike");
    
    
     */
    
}
