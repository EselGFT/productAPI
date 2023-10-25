package com.gfttraining.productAPI;

import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class ProductApiApplication {


	public static void main(String[] args) {
		SpringApplication.run(ProductApiApplication.class, args);
	}

}
