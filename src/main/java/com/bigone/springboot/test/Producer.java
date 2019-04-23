package com.bigone.springboot.test;

import com.bigone.springboot.util.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class Producer {

    public static void sendByEcchange(String message) throws Exception{

        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        //声明队列
        channel.queueDeclare(ConnectionUtil.QUEUE_NAME,true,false,false,null);
        //声明exchange
        channel.exchangeDeclare(ConnectionUtil.EXCHANGE_NAME,"fanout");
        //交换机和队列绑定
        channel.queueBind(ConnectionUtil.QUEUE_NAME,ConnectionUtil.EXCHANGE_NAME,"");
        channel.basicPublish(ConnectionUtil.EXCHANGE_NAME,"",null,message.getBytes());
        System.out.println("发送信息为:" + message);
        channel.close();
        connection.close();

    }

    public static void main(String[] args) {
        try {
            for (int i = 0 ;i<5000; i++)
            sendByEcchange("Hello:"+i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
