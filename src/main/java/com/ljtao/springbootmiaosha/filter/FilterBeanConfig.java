package com.ljtao.springbootmiaosha.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Configuration
public class FilterBeanConfig {
    /*
    1、构造filter
    2、配置过滤urlPattern
    3、利用FilterRegistrationBean进行包装
    4、可以记录所有经过的url
     */
    @Bean
    public FilterRegistrationBean logFilter(){

        FilterRegistrationBean filterRegistrationBean=new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new LogFilter());
        List<String> urList=new ArrayList<>();
        urList.add("*");
        filterRegistrationBean.setUrlPatterns(urList);
        return filterRegistrationBean;
    }

    // 如果是配置servlet 则用 ServletRegistrationBean 来注册。。不过一般不适用这种方式，都是用注解@Controller或者是@RestController
}
