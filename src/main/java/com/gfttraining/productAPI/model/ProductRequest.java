package com.gfttraining.productAPI.model;

import lombok.Data;

@Data
public class ProductRequest {

    private String name;
    private String description;
    private String category;
    private Double price;
    private int stock;

    public ProductRequest(){

    }
    
    public ProductRequest(String name, String description, String category, Double price, int stock) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }
    
    
}