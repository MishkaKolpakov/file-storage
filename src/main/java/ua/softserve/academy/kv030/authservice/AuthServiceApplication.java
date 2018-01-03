package ua.softserve.academy.kv030.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import ua.softserve.academy.kv030.authservice.config.RabbitMQConfig;


@SpringBootApplication
@EnableSwagger2
@EnableScheduling
@EnableAutoConfiguration
@Import(RabbitMQConfig.class)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = {"ua.softserve.academy.kv030.authservice.services", "ua.softserve.academy.kv030.authservice.api.controller",
		"ua.softserve.academy.kv030.authservice.dao", "ua.softserve.academy.kv030.authservice.entity", "ua.softserve.academy.kv030.authservice.config",
		"ua.softserve.academy.kv030.authservice.utils", "ua.softserve.academy.kv030.authservice.filters"})
public class AuthServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}
}
