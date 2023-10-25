package com.gfttraining.productAPI;

import com.gfttraining.productAPI.services.ProductService;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductApiApplication {

	private static Logger logger = Logger.getLogger(ProductService.class);
	public static void main(String[] args) {
		SpringApplication.run(ProductApiApplication.class, args);
		logger.info("Application has started to run");
	}

}
