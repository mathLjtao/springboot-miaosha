package com.ljtao.springbootmiaosha.aop.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

/**
 * @author ljtao3 on 2020/3/13
 * 测试aop
 */
@Aspect
@Configuration
public class TestAspect {

    //定义切入点，这里可以有多种表达式
    @Pointcut("execution(* com.ljtao.springbootmiaosha.test.TestRestController.fun8(..))")
    public void pointcutService(){}

    @Before("pointcutService()")
    public void before(){
        System.out.println("aspect --before----------");
    }
}
