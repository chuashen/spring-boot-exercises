package com.bigone.springboot.rabbitmq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TestListener {

//    @RabbitListener(queues = "testQueue")
//    public void get(String message) throws Exception{
//        System.out.println("监听消费消息：" + message);
//    }

    //containerFactory:指定我们刚刚配置的容器
    @RabbitListener(queues = "testQueue3",containerFactory = "simpleRabbitListenerContainerFactory2")
    public void get(Message message, Channel channel) throws Exception{
//        System.out.println("监听消费消息：" + message);
//        System.out.println(new String(message.getBody(),"UTF-8"));
//        System.out.println(message.getBody());
        String msg = new String(message.getBody(),"UTF-8");
        //调用逻辑处理方法，下单成功，代表该条消息可以确认被消费
        boolean f = placeAnOrder(msg);
        if (f){
            //第一个参数由rabbitmq自己维护，取过来用就行
            //第二个参数设置是否批量处理消息
//            System.out.println("---------确认消费------------");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),true);
        }else{
            //订单处理失败
            //退回消息，通知rabbitmq
            //第三个参数设置是否退回原队列，否时消息作废
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),true,false);
            System.out.println("---------退回消息------------");
            //单个退回，场景被basicNack覆盖，用的少
            //channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);

        }
    }

    private boolean placeAnOrder(String msg) {
        if(msg.contains("121")){
            System.out.println(msg);
            return false;
        }

        return true;
    }


}
