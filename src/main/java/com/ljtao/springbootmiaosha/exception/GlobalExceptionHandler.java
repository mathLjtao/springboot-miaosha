package com.ljtao.springbootmiaosha.exception;

import com.ljtao.springbootmiaosha.util.JsonData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
/**
 * 全局异常处理器
 * @ControllerAdvice or @RestControllerAdvice 增强controller的扩展功能
 * @ExceptionHandler 统一处理某一异常
 * @ResponseStatus 指定客户端收到的http状态码，设置500，客户端就显示500错误
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private static final Logger logger= LoggerFactory.getLogger(GlobalExceptionHandler.class);
    //@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
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
