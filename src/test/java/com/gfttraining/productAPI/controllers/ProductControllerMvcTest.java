package com.gfttraining.productAPI.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.services.ProductService;

@WebMvcTest(ProductController.class)
public class ProductControllerMvcTest {

    private final MockMvc mockMvc;
    
    @MockBean
    ProductService productService;

    public ProductControllerMvcTest(WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
       
    }


    @Test
    void postMappingTest() throws Exception {
        String productName = "TestProduct";
        String productDescription = "TestDescription";
        String categoryName = "TestCategory";
        Double productPrice = 10.0;
        int productStock = 50;    
        Double productWeight = 1.0;


        Product product = new Product(productName, productDescription, new Category("other",0.0), productPrice, productStock, productWeight);
        ProductRequest productRequest = new ProductRequest(productName, productDescription, categoryName, productPrice, productStock, productWeight);
        Mockito.when(productService.createProduct(productName, productDescription, categoryName, productPrice, productStock, productWeight)).thenReturn(product);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(productRequest))
                .characterEncoding("utf-8"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("@.name").value(productName))
            .andExpect(MockMvcResultMatchers.jsonPath("@.description").value(productDescription))
            .andExpect(MockMvcResultMatchers.jsonPath("@.category.name").value("other"))
            .andExpect(MockMvcResultMatchers.jsonPath("@.category.discount").value(0.0))
            .andExpect(MockMvcResultMatchers.jsonPath("@.price").value(productPrice))
            .andExpect(MockMvcResultMatchers.jsonPath("@.stock").value(productStock));
        

    }
}
