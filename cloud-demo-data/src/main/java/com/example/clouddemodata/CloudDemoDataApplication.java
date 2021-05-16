package com.example.clouddemodata;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(scanBasePackages = {"com.example.clouddemodata",
		"com.example.clouddemocommon",})
@EnableEurekaClient
public class CloudDemoDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudDemoDataApplication.class, args);
	}

}
