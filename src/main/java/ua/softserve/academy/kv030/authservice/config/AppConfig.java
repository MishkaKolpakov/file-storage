package ua.softserve.academy.kv030.authservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import ua.softserve.academy.kv030.authservice.converter.StringToOffsetDateTime;
import ua.softserve.academy.kv030.authservice.properties.StatisticProperties;
import ua.softserve.academy.kv030.authservice.services.mail.EmailContentHolder;
import ua.softserve.academy.kv030.authservice.services.rabbit.RabbitMQProducer;
import ua.softserve.academy.kv030.authservice.services.rabbit.RabbitMQProducerImpl;
import ua.softserve.academy.kv030.authservice.utils.JwtUtil;

@EnableConfigurationProperties(StatisticProperties.class)
@Configuration
public class AppConfig {

    @Bean
    @Scope("prototype")
    public Logger logger(InjectionPoint ip) {
        return LoggerFactory.getLogger(ip.getMember().getDeclaringClass());
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Do any additional configuration here
        return builder.build();
    }

    @Bean(initMethod = "init")
    public EmailContentHolder emailContentHolder() {
        return new EmailContentHolder();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }

    @Bean
    public StringToOffsetDateTime stringToOffsetDateTime() {
        return new StringToOffsetDateTime();
    }

    @ConditionalOnProperty(prefix = "statistics",
            name = "enabled", havingValue = "false")
    @Bean
    public RabbitMQProducer rabbitMQProducer() {
        return null;
    }
    @Bean
    public TwitterFactory twitterFactory() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(System.getenv("TWITTER_KEY"))
                .setOAuthConsumerSecret(System.getenv("TWITTER_SECRET"))
                .setOAuthAccessToken(System.getenv("TWITTER_TOKEN"))
                .setOAuthAccessTokenSecret(System.getenv("TWITTER_TOKEN_SECRET"));
        return new TwitterFactory(cb.build());
    }


}
