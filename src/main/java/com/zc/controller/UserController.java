package com.zc.controller;

import com.zc.domain.User;
import com.zc.domain.Users;
import com.zc.services.impl.UserserviceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: 山毛榉
 * @date : 2022/6/20 17:06
 * @version: 1.0
 */
@Controller
public class UserController {
    @Autowired
    UserserviceImpl userservice;

    @RequestMapping("/sayHello.jsp")


    public String sayHello(){


        String name = null;
        System.out.println("你好"+name+"天气很好");
        return "helloworld";


    }
    @ResponseBody
    @RequestMapping("/query.do")
    public String getUser(HttpServletRequest request){
        System.out.println("hello world ------------------");
        String id = request.getParameter("id");

        User user = userservice.getUser();
        System.out.println(user.toString());
        Users u = userservice.getU(new Integer(id));
        System.out.println(u);
        System.out.println(user.toString());

        return u.toString();
    }
}
