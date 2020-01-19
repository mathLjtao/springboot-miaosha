package com.ljtao.springbootmiaosha.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;
/*
    日志过滤器。
    将所有的访问链接，都记录在日志中
 */
public class LogFilter implements Filter {
    private Logger logger= LoggerFactory.getLogger(LogFilter.class);
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.info("request--coming");
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
