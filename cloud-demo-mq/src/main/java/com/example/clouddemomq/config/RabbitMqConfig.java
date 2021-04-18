package com.example.clouddemomq.config;
import com.example.clouddemocommon.utils.ValidationUtil;
import com.example.clouddemomq.constant.ExQuBindEnum;
import com.example.clouddemomq.constant.VirtualHostConstant;
import com.rabbitmq.http.client.Client;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.index.PathBasedRedisIndexDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * des 主要使用springboot 提供的rabbitAdmin
 * 管理所有的绑定，配置
 * rabbitmq 配置类
 * @author yukefu
 * @date 2021/1/30 11:20
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq")
@Slf4j
public class RabbitMqConfig {
    /** 服务地址*/
    private String host;
    /** 端口号*/
    private Integer port;
    /** 用户名*/
    private String username;
    /** 用户密码*/
    private String password;
    /**虚拟主机*/
    /*private String virtualHost = "/";*/

    private Integer managementPort = 15672;



    /**
     * method desc
     * springboot 默认使用 CachingConnectionFactory 工厂
     * @param
     * @return {@code org.springframework.amqp.rabbit.connection.ConnectionFactory}
     * @author ykf
     * @date 2021/1/30 11:52
     *
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        setCommon(connectionFactory);
        connectionFactory.setVirtualHost(VirtualHostConstant.ADMIN_GAME);
        return connectionFactory;
    }

    /**
     * method desc
     * 配置工厂
     * @param connectionFactory
     * @return {@code void}
     * @author ykf
     * @date 2021/1/30 12:02
     *
     */
    private void setCommon(CachingConnectionFactory connectionFactory) {
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
    }

    /**
     * method desc
     * 设置默认监听消息
     * @param configurer
     * @param connectionFactory
     * @return {@code org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory}
     * @author ykf
     * @date 2021/1/30 11:58
     *
     */
    @Bean
    public SimpleRabbitListenerContainerFactory vipFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer,
                                                           @Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }


    /**
     * method desc
     * 发送，接受模板对象
     * @param
     * @return {@code org.springframework.amqp.rabbit.core.RabbitTemplate}
     * @author ykf
     * @date 2021/1/30 11:59
     *
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * method desc
     * rabbitMq管理组件 里面有对队列，exchange 处理的一些方法，比如 binding delete.* 具体查看父类
     * org.springframework.amqp.core.AmqpAdmin
     * @param
     * @return {@code org.springframework.amqp.rabbit.core.RabbitAdmin}
     * @author ykf
     * @date 2021/1/30 11:59
     *
     */
    @Bean
    public RabbitAdmin rabbitAdmin() {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
        this.exchangeQueueBindToRabbitAdmin(rabbitAdmin);
        return rabbitAdmin;
    }

    /**
     * method desc
     * 消息装换类
     * @param
     * @return {@code org.springframework.amqp.support.converter.MessageConverter}
     * @author ykf
     * @date 2021/1/30 12:02
     *
     */
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }


    /**
     * method desc
     * 交给RabbitAdmin 管理 注意没有特殊业务的话 因为性能关系 优先选择
     * ExchangeTypes:fanout>direct>topic >headers后面两种不常用
     * @param rabbitAdmin 
     * @return {@code void}
     * @author ykf
     * @date 2021/1/30 12:09 
     *
     */
    private void exchangeQueueBindToRabbitAdmin(RabbitAdmin rabbitAdmin) {
        log.info("===========>交换机以及队列开始绑定");
        Map<String, Object> arguments = null;
        Exchange exchange = null;
        ExQuBindEnum[] rabbitMQServiceEnum = ExQuBindEnum.values();
        //遍历枚举绑定交换机绑定队列
        for (int i = 0; i < rabbitMQServiceEnum.length; i++) {
            ExQuBindEnum rabbitEnum = rabbitMQServiceEnum[i];
            exchange = convertExchange(rabbitEnum,arguments);
            ValidationUtil.assertNotNull(exchange,"不支持这个交换类型");
            Queue queue = new Queue(rabbitEnum.getMqName(), true, false, false, arguments);
            rabbitAdmin.declareExchange(exchange);
            rabbitAdmin.declareQueue(queue);
            rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(rabbitEnum.getKeyName()).noargs());
        }
        log.info("<===========结束绑定交换机以及队列");
    }

    /**
     * 配置 rabbitmq-management 端口
     * @return
     */
    @Bean
    @Qualifier("rabbitHttpClient")
    public Client client() {
        String uri = String.format("http://%s:%s/api/", host, managementPort);
        Client client = null;
        try {
            client = new Client(uri, username, password);
            client.getVhosts();
        } catch (Exception e) {
            log.error("[rabbitmq-management配置有误]请打开RabbitMQ {} 端口", managementPort, e);
        }
        return client;
    }

    /**
     * method desc
     * 适配相对应的交换机
     * @param rabbitEnum
     * @param arguments
     * @return {@code org.springframework.amqp.core.Exchange}
     * @author ykf
     * @date 2021/1/30 14:13
     *
     */
    private Exchange convertExchange(ExQuBindEnum rabbitEnum, Map<String, Object> arguments){
        Boolean isDelayQueue = rabbitEnum.getIsDelayQueue();
        ValidationUtil.assertNotNull(isDelayQueue,"是否延迟请勿用null覆盖");
        String exchangeTypes = rabbitEnum.getExchangeTypes();
        ValidationUtil.assertNotNull(isDelayQueue,"交换机类型迟请勿用null覆盖");
        if (isDelayQueue){
            arguments = new HashMap<>();
            arguments.put("x-delayed-type", ExchangeTypes.FANOUT);
            return new CustomExchange(rabbitEnum.getExName(), "x-delayed-message", true, false, arguments);
        }
        switch (exchangeTypes){
            case ExchangeTypes.FANOUT:
                return new FanoutExchange(rabbitEnum.getExName(), true, false);
            case ExchangeTypes.DIRECT:
                return new DirectExchange(rabbitEnum.getExName(),true, false);
            case ExchangeTypes.TOPIC:
                return new TopicExchange(rabbitEnum.getExName(), true, false);
            default:
                return null;
        }
    }
}