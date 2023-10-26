package com.gfttraining.productAPI.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message= "The product's name can't be blank")
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn
    @NotNull(message = "The product's category can't be null")
    private Category category;
    
    @NotNull(message = "The product's price can't be null")
    @Min(value = 0, message="The product's price can't be less than 0")
    private Double price;

    @NotNull(message = "The product's stock can't be null")
    @Min(value = 0, message="The product's stock can't be less than 0")
    private int stock;

    @NotNull(message = "The product's weight can't be null")
    @Min(value = 0, message="The product's weight can't be less than 0")
    private Double weight;

    public Product(ProductRequest productRequest, Category category) {
        this.name = productRequest.getName();
        this.description = productRequest.getDescription();
        this.category = category;
        this.price = productRequest.getPrice();
        this.stock = productRequest.getStock();
        this.weight = productRequest.getWeight();
    }

    public Product(String name, String description, Category category, Double price, int stock, Double weight) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.weight = weight;
    }
}