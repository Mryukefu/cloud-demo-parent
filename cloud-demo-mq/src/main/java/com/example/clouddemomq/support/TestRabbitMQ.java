package com.example.clouddemomq.support;

import com.example.clouddemocommon.entry.constant.QueueNameConstant;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@RabbitListener(queues = QueueNameConstant.QUEUE_FANOUT_TEST)
@Component
public class TestRabbitMQ extends AbstractRabbitSupport{

    @RabbitHandler
    private void loginReceive(Map map, Message message, Channel channel) {
        listenUp(QueueNameConstant.QUEUE_FANOUT_TEST,
                message,
                channel,
                (Integer) map.get("id"),
                "异常",
               t1-> System.out.println(t1)

        );
    }

}
