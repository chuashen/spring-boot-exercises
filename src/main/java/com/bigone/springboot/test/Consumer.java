package com.bigone.springboot.test;

import com.bigone.springboot.util.ConnectionUtil;
import com.rabbitmq.client.*;

import java.io.IOException;

public class Consumer {

    public static void getMessage() throws Exception{
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        DefaultConsumer deliverCallback = new DefaultConsumer(channel){

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(new String(body,"UTF-8"));
            }
        };

        channel.basicConsume(ConnectionUtil.QUEUE_NAME,deliverCallback);

    }

    public static void main(String[] args) {
        try {
            getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
