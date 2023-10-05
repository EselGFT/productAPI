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
    private int id;

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name ="category.name")
    private Category category; // String category
    private Double price;
    private int stock;

    public Product(String name, String description, Category category, Double price, int stock) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }
    // public Product(String name, String description, String category, Double price, int stock) {
        
        
    //     this.name = name;
    //     this.description = description;
    //     this.category = category;
    //     this.price = price;
    //     this.stock = stock;
    // }



    public Product(){

    }
 
    

}
