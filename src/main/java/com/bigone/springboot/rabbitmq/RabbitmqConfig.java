package com.bigone.springboot.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitmqConfig {

    @Bean
    public ConnectionFactory connectionFactory(){

        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory("192.168.177.226",5672);
        connectionFactory.setUsername("mqadmin");
        connectionFactory.setPassword("mqadmin");
        connectionFactory.setVirtualHost("testhost");
        //是否开启消息确认机制,确认消息送达mq路由器
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;

    }

    @Bean
    public DirectExchange defaultExchange(){
        return new DirectExchange("directExchange");
    }

    @Bean
    public Queue queue(){
        //名字 是否持久化
        return new Queue("testQueue",true);
    }

    @Bean
    public Binding binding(){
        //绑定一个队列 to：绑定到哪个交换机上面 with：绑定的路由建（routingkey）
        return BindingBuilder.bind(queue()).to(defaultExchange()).with("direct.key");
    }

    @Bean
    public RabbitTemplate rabbitTemplate (ConnectionFactory connectionFactory){
        //注意 这个ConnectionFactory 是使用javaconfig方式配置连接的时候才需要传入的
        // 如果是yml配置的连接的话是不需要的
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        //开启mandatory模式（开启失败回调）
        template.setMandatory(true);
        //指定失败回调接口的实现类，路由失败会回调，确认发送到队列成功
        template.setReturnCallback(new MyReturnCallback());
        //设置提交确认回调函数，确认发送到mq成功
        template.setConfirmCallback(new MyConfirmCallback());
        return template;
    }


    /**
     * 设置消息手动确认
     * 声明一个监听器容器
     * */
    @Bean
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        //这个connectionFactory就是我们自己配置的连接工厂直接注入进来
        simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
        //设置消息确方式由自动确认边为手动确认
        simpleRabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return simpleRabbitListenerContainerFactory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory2(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
        //设置消息确方式由自动确认边为手动确认
        simpleRabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        //设置消息预取的数量
        //预取数量大小与性能成正比，与可数据靠性成反比，1到2500，中间数500
        //默认的轮训机制会一次性由mq服务端发送消息到监听消费端，不适用集群场景
        //设置预取，消息全部确认后再发送
        simpleRabbitListenerContainerFactory.setPrefetchCount(500);
        return simpleRabbitListenerContainerFactory;
    }

    /**
     * 死信交换机，用于存储废弃的消息
     * */

    @Bean
    public DirectExchange deadExchange(){
        return new DirectExchange("dead_Exchange");
    }

    @Bean
    public Queue queue_dead(){
        return new Queue("deadQueue",true);
    }

    @Bean
    public Binding binding2(){
        return BindingBuilder.bind(queue_dead()).to(deadExchange()).with("dead.order");
    }

    @Bean
    public Queue queue2(){
        Map<String,Object> map = new HashMap<>();
        //设置消息的过期时间 单位毫秒
        map.put("x-message-tt1",10000);
        //设置附带的死信交换机
        map.put("x-dead-letter-exchange","dead_Exchange");
        //指定重定向的路由建 消息作废之后可以决定需不需要更改他的路由建 如果需要 就在这里指定
        map.put("x-dead-letter-routing-key","dead.order");
        return new Queue("testQueue3", true,false,false,map);
    }

    @Bean
    public Binding binding21(){
        return BindingBuilder.bind(queue2()).to(defaultExchange()).with("direct.key");
    }

}
