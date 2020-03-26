package com.ljtao.springbootmiaosha.controller;

import com.ljtao.springbootmiaosha.dao.UserMapper;
import com.ljtao.springbootmiaosha.model.User;
import com.ljtao.springbootmiaosha.rabbitmq.MQSender;
import com.ljtao.springbootmiaosha.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author ljtao3 on 2020/3/25
 */
@RestController
@RequestMapping("/recordWord")
public class RecordWorkController {
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserMapper userMapper;

    /*
    1000 线程 ，（每秒处理的请求）QPS 80+
    2000 线程，QPS 70+，这时已经出现了错误请求了
     */
    @PostMapping("/inWork")
    public JsonData inWork(@RequestBody User user){
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        userMapper.insert(user);
        return JsonData.success(user);
    }
    /*
        利用MQ队列进行优化，
        1000 线程 ，QPS 540+  响应速度提升7倍左右，并且数据存入无误。
        2000 线程，QPS 1000+，响应速度提升13倍左右，有点叼，还是没有出现错误 ，数据也都完整保存在数据库。
        5000 线程 QPS 937，请求响应完毕后，数据陆续在MQ队列中保存到数据库，中途继续发起1000线程，数据依然完整保存在数据库。

        10000 线程，QPS700+，出现请求错误，出现瓶颈了，下次弄负载均衡再试试。
        直接20000 线程 走起，QPS 846,出现了请求错误，错误率18.48%。
        结论，哔了狗了，rabbitMQ真叼，提高了请求的响应速度，还能保证数据完整保存在数据库。
     */
    /*
        在MQ处理数据到数据库的过程中，
        关闭服务器后重开，还是会陆续插入数据到数据库。
        关闭MQ重启，数据后面也会陆续插入到数据库。

        服务器跟MQ一起宕机，才会出现数据存储异常
     */
    @PostMapping("/inWork2")
    public JsonData inWork2(@RequestBody User user){

        mqSender.inWordRecordSend(JsonData.beanToString(user));

        return JsonData.success(user);
    }

}
