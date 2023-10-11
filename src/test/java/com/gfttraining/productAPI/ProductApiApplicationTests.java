package com.gfttraining.productAPI;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductApiApplicationTests {
	@Test
	void testNoExceptionInExecution() {
		assertDoesNotThrow(() -> ProductApiApplication.main(new String[]{}));
	}

}
