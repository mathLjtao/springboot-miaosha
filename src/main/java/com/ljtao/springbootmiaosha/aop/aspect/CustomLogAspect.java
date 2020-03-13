package com.ljtao.springbootmiaosha.aop.aspect;



import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @Description: 自定义切面
 * @author ljtao3 on 2020/3/13
 */
//@Aspect//暂不开启
//@Configuration//暂不开启
public class CustomLogAspect {
    private Logger logger = LoggerFactory.getLogger(CustomLogAspect.class);

    /**
     * @Description: 定义切入点
     * @Title: pointCut
     */
    //被注解CustomAopAnnotation表示的方法
    //@Pointcut("@annotation(com.ljtao.springbootmiaosha.annotation.CustomAopAnnotation")
    @Pointcut("execution(public * com.ljtao.springbootmiaosha.test.*.*(..))")
    public void pointCut(){

    }

    /**
     * @Description: 定义前置通知
     * @Title: before
     * @param joinPoint
     * @throws Throwable
     */
    @Before("pointCut()")
    public void before(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        logger.info("【注解：Before】------------------切面  before");
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
        logger.info("【注解：Before】浏览器输入的网址=URL : " + request.getRequestURL().toString());
        logger.info("【注解：Before】HTTP_METHOD : " + request.getMethod());
        logger.info("【注解：Before】IP : " + request.getRemoteAddr());
        logger.info("【注解：Before】执行的业务方法名=CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        logger.info("【注解：Before】业务方法获得的参数=ARGS : " + Arrays.toString(joinPoint.getArgs()));
        System.out.println("before");
    }

    /**
     * @Description: 后置返回通知
     * @Title: afterReturning
     * @param ret
     * @throws Throwable
     */
    @AfterReturning(returning = "ret", pointcut = "pointCut()")
    public void afterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
        logger.info("【注解：AfterReturning】这个会在切面最后的最后打印，方法的返回值 : " + ret);
    }

    /**
     * @Description: 后置异常通知
     * @Title: afterThrowing
     * @param jp
     */
    @AfterThrowing("pointCut()")
    public void afterThrowing(JoinPoint jp){
        logger.info("【注解：AfterThrowing】方法异常时执行.....");
    }

    /**
     * @Description: 后置最终通知,final增强，不管是抛出异常或者正常退出都会执行
     * @Title: after
     * @param jp
     */
    @After("pointCut()")
    public void after(JoinPoint jp){
        logger.info("【注解：After】方法最后执行.....");
    }

    /**
     * @Description: 环绕通知,环绕增强，相当于MethodInterceptor
     * @Title: around
     * @param pjp
     * @return
     */
    @Around("pointCut()")
    public Object around(ProceedingJoinPoint pjp) {
        logger.info("【注解：Around . 环绕前】方法环绕start.....");
        try {
            //如果不执行这句，会不执行切面的Before方法及controller的业务方法
            Object o =  pjp.proceed();
            logger.info("【注解：Around. 环绕后】方法环绕proceed，结果是 :" + o);
            return o;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

}