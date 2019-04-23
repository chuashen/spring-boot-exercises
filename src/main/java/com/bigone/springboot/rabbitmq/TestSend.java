package com.bigone.springboot.rabbitmq;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TestSend {

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void testSend(String message){
        System.out.println("发送消息：" + message);
        //添加发送确认标识信息
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("directExchange","direct.key", message,correlationData);
    }




}
