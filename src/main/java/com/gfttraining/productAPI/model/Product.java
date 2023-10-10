package com.gfttraining.productAPI.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class Product {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn
    private Category category; 
    private Double price;
    private int stock;

    public Product(){
    }

    public Product(String name, String description, Category category, Double price, int stock) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

}
