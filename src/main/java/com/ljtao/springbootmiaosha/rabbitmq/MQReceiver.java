package com.ljtao.springbootmiaosha.rabbitmq;

import com.ljtao.springbootmiaosha.domian.MiaoshaMessage;
import com.ljtao.springbootmiaosha.model.OrderInfo;
import com.ljtao.springbootmiaosha.model.User;
import com.ljtao.springbootmiaosha.service.GoodsService;
import com.ljtao.springbootmiaosha.service.MiaoshaOrderService;
import com.ljtao.springbootmiaosha.util.JsonData;
import com.ljtao.springbootmiaosha.vo.GoodsVo;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/*
    消息接收者
 */
@Service
public class MQReceiver {

    @Autowired
    private GoodsService goodsService;
    private MiaoshaOrderService miaoshaOrderService;

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void miaoshaReceive(String message){
        try{
            //这里让线程停止一秒，前端页面等待效果会明显一些
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }

        MiaoshaMessage miaoshaMessage = JsonData.stringToBean(message, MiaoshaMessage.class);
        GoodsVo goodsVo = goodsService.getMiaoshaGoodsById(miaoshaMessage.getGoodsId());
        User user = miaoshaMessage.getUser();
        try{
            goodsService.optimizeHandleMiaosha(user, goodsVo);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = MQConfig.TEXT_QUEUE_NAME)
    public void textReceive(String msg){
        System.out.println("receive:"+msg);
    }
    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void testReceive1(String msg){
        System.out.println("topic queue1 receive:"+msg);
    }
    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void testReceive2(String msg){
        System.out.println("topic queue2 receive:"+msg);
    }
    @RabbitListener(queues = MQConfig.HEADER_QUEUE1)
    public void testReceive3(byte[] msg){
        System.out.println("header queue1 receive:"+new String(msg));
    }
}
