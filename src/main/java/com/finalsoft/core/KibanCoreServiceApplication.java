package com.finalsoft.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient

@EntityScan({ "com.finalsoft.common.domain.persistence"})
@ComponentScan({"com.finalsoft.common.mapper"})
public class KibanCoreServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(KibanCoreServiceApplication.class, args);
	}

}
