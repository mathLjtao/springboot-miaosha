package com.ljtao.springbootmiaosha.access;

import com.ljtao.springbootmiaosha.redis.RedisService;
import com.ljtao.springbootmiaosha.service.UserService;
import com.ljtao.springbootmiaosha.util.CodeMsg;
import com.ljtao.springbootmiaosha.util.JsonData;
import com.ljtao.springbootmiaosha.util.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
/*
设置自定义拦截器，对有注解@AccessLimit这个的方法，进行处理
 */
@Service
public class AccessLimitInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RedisTemplate redisTemplate;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response
            , Object handler) throws Exception {
        //排除对静态资源的拦截处理
        if(handler instanceof  ResourceHttpRequestHandler){
            return true;
        }
        HandlerMethod hm=(HandlerMethod)handler;
        AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
        if(accessLimit==null){
            return true;
        }
        String userIp= RequestUtil.getIpAddress(request);
        String uri=request.getRequestURI();
        long seconds = accessLimit.seconds();
        long maxCount = accessLimit.maxCount();



        //设置api访问限制
        Long callCount = (Long)redisService.get(RedisService.ACCESS_LIMIT + userIp + "_" + uri);
        if(callCount==null){
            redisService.set(RedisService.ACCESS_LIMIT + userIp + "_" + uri,1l,seconds);
        }else if(callCount<maxCount) {
            //这个递增需要设置，但是这设置之后，只能保存字符串，具体参考这个https://blog.csdn.net/wangjun5159/article/details/52387782
            /*
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(new StringRedisSerializer());
            redisTemplate.boundValueOps(RedisService.ACCESS_LIMIT + userIp + "_" + uri).increment(1l);
            */
            //所以使用下面这个虽然 有点瑕疵，但没什么关系
            redisService.set(RedisService.ACCESS_LIMIT + userIp + "_" + uri,callCount+1,seconds);
        }else{
            render(response,CodeMsg.ACCESS_LIMIT_REACHED.getMsg());
            return false;
        }
        return true;
    }

    private void render(HttpServletResponse response, String msg) throws  Exception{
        response.setContentType("application/json;charset=UTF-8");
        OutputStream outputStream = response.getOutputStream();
        String str=JsonData.beanToString(JsonData.fail(msg));
        outputStream.write(str.getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }

}
