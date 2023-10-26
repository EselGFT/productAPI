package com.gfttraining.productAPI.controllers;

import com.gfttraining.productAPI.exceptions.NonExistingProductException;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("GIVEN the information to create a product WHEN the create product endpoint is called THEN the product is created in the database with the data indicated")
    void postMappingTest() throws Exception {
        String productName = "TestProduct";
        String productDescription = "TestDescription";
        String categoryName = "TestCategory";
        Double productPrice = 10.0;
        int productStock = 50;    
        Double productWeight = 1.0;

        ProductRequest productRequest = new ProductRequest(productName, productDescription, categoryName, productPrice, productStock, productWeight);
        Product product = new Product(productRequest, new Category("other", 0.0));
        Mockito.when(productService.createProduct(productRequest)).thenReturn(product);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/products")
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
    @DisplayName("WHEN the list all products endpoint is called THEN a list containing all the products in the database is returned")
    void listAllMappingTest() throws Exception {

        Product apple = new Product("Apple", "A rounded food object", new Category("food", 25.0), 1.25, 23 ,1.0);
        Product dictionary = new Product("Dictionary", "A book that defines words", new Category("books", 15.0), 19.89, 13,1.0);

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

    @Test
    @DisplayName("WHEN retrieving a product GIVEN its ID THEN the object is returned")
    void listOneProductTest() throws Exception {
        Product apple = new Product("Apple", "A rounded food object", new Category("food", 25.0), 1.25, 23 ,1.0);

        Mockito.when(productService.listProductById(0)).thenReturn(apple);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/products/0")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("@.name").value("Apple"))
            .andExpect(MockMvcResultMatchers.jsonPath("@.description").value("A rounded food object"))
            .andExpect(MockMvcResultMatchers.jsonPath("@.category.name").value("food"))
            .andExpect(MockMvcResultMatchers.jsonPath("@.category.discount").value(25.0))
            .andExpect(MockMvcResultMatchers.jsonPath("@.price").value(1.25))
            .andExpect(MockMvcResultMatchers.jsonPath("@.stock").value(23));

    }

    @Test
    @DisplayName("WHEN performing a get request for one product GIVEN a non existent ID THEN the exception text is returned")
    void listOneNonExistentProductTest() throws Exception{
        Mockito.when(productService.listProductById(1)).thenThrow(new NonExistingProductException("no"));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/products/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("@").value("no"));

    }

}