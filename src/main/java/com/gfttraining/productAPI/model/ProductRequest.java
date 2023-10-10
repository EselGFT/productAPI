package com.gfttraining.productAPI.model;

import lombok.Data;

@Data
public class ProductRequest {

    private String name;
    private String description;
    private String category;
    private Double price;
    private int stock;
    
}