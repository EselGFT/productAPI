package com.gfttraining.productAPI.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.services.ProductService;

import java.util.Arrays;
import java.util.List;

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

        Product product = new Product(productName, productDescription, new Category("other",0.0), productPrice, productStock);
        ProductRequest productRequest = new ProductRequest(productName, productDescription, categoryName, productPrice, productStock);
        Mockito.when(productService.createProduct(productName, productDescription, categoryName, productPrice, productStock)).thenReturn(product);

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

    @Test
    void listAllMappingTest() throws Exception {

        Product apple = new Product("Apple", "A rounded food object", new Category("food", 25.0), 1.25, 23);
        Product dictionary = new Product("Dictionary", "A book that defines words", new Category("books", 15.0), 19.89, 13);

        List<Product> products = Arrays.asList(apple, dictionary);

        Mockito.when(productService.listProducts()).thenReturn(products);

        mockMvc.perform(MockMvcRequestBuilders
                 .get("/products")
                 .contentType(MediaType.APPLICATION_JSON)
                 .characterEncoding("utf-8"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("@.[0].name").value("Apple"))
            .andExpect(MockMvcResultMatchers.jsonPath("@.[1].name").value("Dictionary"))
            .andExpect(MockMvcResultMatchers.jsonPath("@.[0].category.name").value("food"))
            .andExpect(MockMvcResultMatchers.jsonPath("@.[1].stock").value(13));
    }
}