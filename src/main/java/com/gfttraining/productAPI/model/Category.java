package com.gfttraining.productAPI.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Category {

    @Id
    @NotBlank(message = "Name should not be blank")
    private String name;

    @NotNull(message = "Discount should not be null")
    @Min(value = 0, message ="Discount should not be less than 0")
    @Max(value = 100, message= "Discount should not be more than 100")
    private double discount;

    public Category() {}
    public Category(String name, double discount) {
        this.name = name;
        this.discount = discount;
    }

}

