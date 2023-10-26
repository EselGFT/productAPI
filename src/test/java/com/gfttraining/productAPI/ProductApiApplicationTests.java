package com.gfttraining.productAPI;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductApiApplicationTests {
	@Test
	@DisplayName("WHEN the application is executed THEN it does not throw any exception")
	void testNoExceptionInExecution() {
		assertDoesNotThrow(() -> ProductApiApplication.main(new String[]{}));
	}

}
