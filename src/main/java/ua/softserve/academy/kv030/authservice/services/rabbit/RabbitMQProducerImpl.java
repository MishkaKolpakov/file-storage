package ua.softserve.academy.kv030.authservice.services.rabbit;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ua.softserve.academy.kv030.authservice.entity.Statistic;
import ua.softserve.academy.kv030.authservice.properties.StatisticProperties;


@Service
public class RabbitMQProducerImpl implements RabbitMQProducer {

    private final StatisticProperties properties;


    @Autowired
    public RabbitMQProducerImpl(StatisticProperties properties) {
        this.properties = properties;
    }

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public void send(Statistic statistic){
        amqpTemplate.convertAndSend(this.properties.getExchange(), this.properties.getKey(), statistic);
    }
}
