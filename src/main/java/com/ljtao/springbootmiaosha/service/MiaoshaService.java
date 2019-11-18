package com.ljtao.springbootmiaosha.service;

import com.ljtao.springbootmiaosha.model.User;
import com.ljtao.springbootmiaosha.redis.RedisService;
import com.ljtao.springbootmiaosha.util.MD5Util;
import com.ljtao.springbootmiaosha.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
public class MiaoshaService {
    @Autowired
    private RedisService redisService;
    public String createMiaoshaPath(User user, Long goodsId) {
        if(user==null|| goodsId<=0){
            return null;
        }
        String path= MD5Util.md5(UUIDUtil.uuid()+"salt123");
        redisService.set(RedisService.MIAOSHA_PATH+user.getId()+"_"+goodsId,path,60);
        return path;
    }

    public boolean checkMiaoshaPath(User user, Long goodsId, String path) {
        if(user==null || path==null){
            return false;
        }
        String o = (String)redisService.get(RedisService.MIAOSHA_PATH + user.getId() + "_" + goodsId);
        return path.equals(o);

    }

    public BufferedImage createVerifyCode(User user, Long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(RedisService.MiaoshaVerifyCode+ user.getId()+","+goodsId, rnd,300);
        //输出图片
        return image;
    }
    public boolean checkVerifyCode(User user, long goodsId, int verifyCode) {
        if(user == null || goodsId <=0) {
            return false;
        }
        Integer codeOld = (Integer)redisService.get(RedisService.MiaoshaVerifyCode+ user.getId()+","+goodsId);
        if(codeOld == null || codeOld - verifyCode != 0 ) {
            return false;
        }
        redisService.delete(RedisService.MiaoshaVerifyCode+ user.getId()+","+goodsId);
        return true;
    }

    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(exp);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[] {'+', '-', '*'};
    /**
     * + - *
     * */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }
}
