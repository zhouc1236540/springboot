package com.zc.controller;

import com.alibaba.fastjson.JSON;
import com.zc.domain.Users;
import com.zc.services.impl.UserserviceImpl;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * @author: 山毛榉
 * @date : 2022/6/20 17:06
 * @version: 1.0
 */
@Controller

public class UserController {

    UserserviceImpl userservice;
    private  Users user;

    @Autowired
    public void setUserservice(UserserviceImpl userservice) {

        this.userservice = userservice;
    }

    @RequestMapping("/sayHello.jsp")
    @ResponseBody
    public String sayHello(){


        System.out.println("你好天气很好");
        return "hello world";


    }

    @ResponseBody
    @PostMapping("/query.action")
    public String getUser( @RequestBody String id){
       System.out.println("hello world ------------------");
        System.out.println("id = " + id);

         user = JSON.parseObject(id, Users.class);
        System.out.println("users.getId() = " + user.getId());
        System.out.println("我是id"+id);

        Users u = userservice.getU(user.getId());

        return JSON.toJSONString(u);
    }
}
