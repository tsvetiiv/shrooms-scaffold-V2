package com.shrooms.scaffold;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableCaching
@SpringBootApplication
@EnableFeignClients
public class ScaffoldApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScaffoldApplication.class, args);
	}

}
