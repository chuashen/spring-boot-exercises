package com.bigone.springboot.util;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ConnectionUtil {

    public static final String QUEUE_NAME = "testQueue2";

    public static final String EXCHANGE_NAME = "exchange";


    public static Connection getConnection () throws Exception{
        //创建一个连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //设置rabbitmq服务器地址
        connectionFactory.setHost("192.168.177.226");
        //端口、用户名、连接名称、虚拟地址
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("mqadmin");
        connectionFactory.setPassword("mqadmin");
        connectionFactory.setVirtualHost("testhost");
        return  connectionFactory.newConnection();
    }

}
