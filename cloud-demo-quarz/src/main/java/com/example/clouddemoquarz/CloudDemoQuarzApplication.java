package com.example.clouddemoquarz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.clouddemoquarz.dao")
public class CloudDemoQuarzApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudDemoQuarzApplication.class, args);
	}

}
