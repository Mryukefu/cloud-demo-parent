package com.example.clouddemouser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.example.clouddemouser","com.example.clouddemocommon",})
@EnableFeignClients("com.example.clouddemocommon.feign")
@EnableEurekaClient
public class CloudDemoUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudDemoUserApplication.class, args);
	}

}
