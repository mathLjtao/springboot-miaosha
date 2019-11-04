package com.ljtao.springbootmiaosha.test;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/miaosha/testController")
public class TestController {
    /*
    测试返回freemarker，ftl文件页面
     */
    @RequestMapping("/test")
    public String fun1(ModelMap modelMap){
        modelMap.put("name","ljtao3");
        Object obj;
        return "test/test";
    }
    /*
    测试一个例子页面,转到html页面
     */
    @RequestMapping("/testToHtml")
    public String fun2(){
        return "login";
    }
}
