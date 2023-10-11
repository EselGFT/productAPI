package com.gfttraining.productAPI.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Product {

    @Id
    @GeneratedValue
    private long id;

    @NotBlank(message= "Name should not be blank")
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn
    @NotNull
    private Category category;
    
    @NotNull(message = "Price should not be null")
    @Min(value = 0, message="Price should not be less than 0")
    private Double price;

    @NotNull(message = "Stock should not be null")
    @Min(value = 0, message="Stock should not be less than 0")
    private int stock;

    @NotNull(message = "Weight should not be null")
    @Min(value = 0, message="Weight should not be less than 0")
    private Double weight;
    
    public Product(){
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
