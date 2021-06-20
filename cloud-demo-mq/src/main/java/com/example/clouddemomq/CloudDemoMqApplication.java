package com.example.clouddemomq;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.example.clouddemomq","com.example.clouddemocommon"})
@EnableConfigurationProperties
public class CloudDemoMqApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudDemoMqApplication.class, args);
	}

}
