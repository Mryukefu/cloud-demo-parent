package com.example.clouddemomq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class CloudDemoMqApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudDemoMqApplication.class, args);
	}

}
