package ua.softserve.academy.kv030.authservice.services.rabbit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import ua.softserve.academy.kv030.authservice.entity.Statistic;

@ConditionalOnProperty(prefix = "statistics",
        name = "enabled")
public interface RabbitMQProducer {
    void send(Statistic statistic);
}
