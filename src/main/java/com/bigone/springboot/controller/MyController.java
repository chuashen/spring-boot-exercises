package com.bigone.springboot.controller;

import com.bigone.springboot.rabbitmq.TestSend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("my")
public class MyController {

    @Autowired
    private TestSend testSend;


    @RequestMapping(value = "sendMsg")
    @ResponseBody
    public String sendMsg(String msg){
        testSend.testSend(msg);
        return "ok";
    }


}
