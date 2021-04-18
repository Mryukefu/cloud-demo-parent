package com.example.clouddemoeureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class CloudDemoEurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudDemoEurekaApplication.class, args);
	}

}
