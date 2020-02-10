package com.neu.gmall.user.controller;


import com.neu.gmall.bean.UmsMember;
import com.neu.gmall.bean.UmsMemberReceiveAddress;
import com.neu.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("index")
    @ResponseBody
    public String index(){
        return "hello,user.";
    }

    /**
     * 查找数据库中所有用户信息
     */
    @RequestMapping("getAllUser")
    @ResponseBody
    public List<UmsMember>  getAllUser(){
        List<UmsMember> user = userService.getAllUser();
        return  user;
    }

    //@RequestBody输入可以为json格式
    @RequestMapping("getUmsMemberReceiveAddressById")
    @ResponseBody
    public List<UmsMemberReceiveAddress> getUmsMemberReceiveAddressById(String memberId){
        return userService.getUmsMemberReceiveAddressById(memberId);
    }
}
