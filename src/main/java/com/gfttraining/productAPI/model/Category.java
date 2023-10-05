package com.gfttraining.productAPI.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Category {
    @Id
    private String name;
    private double discount;

    public Category() {}
    public Category(String name, double discount) {
        this.name = name;
        this.discount = discount;
    }


    //mapa: name -> discount
}
