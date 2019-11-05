package com.ljtao.springbootmiaosha.exception;

import com.ljtao.springbootmiaosha.util.JsonData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
/*
    全局异常处理器
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private static final Logger logger= LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(Exception.class)
    public JsonData exceptionHandler(HttpServletRequest req, Exception re){
        if(re instanceof ArithmeticException ){
            System.out.println("除数不能为0 : /by zero");
        }
        if(re instanceof NullPointerException  ){
            System.out.println("空指针异常");
        }
        String url=req.getRequestURI();
        String message = re.getMessage();
        logger.error(message);
        logger.error(req.getRequestURL().toString());
        return JsonData.fail(url+":"+message);
    }
}
