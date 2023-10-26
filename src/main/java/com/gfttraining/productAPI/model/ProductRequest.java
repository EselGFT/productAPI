package com.gfttraining.productAPI.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    
    @NotBlank(message = "Name should not be blank")
    private String name;

    private String description;
    
    private String category;

    @NotNull(message = "Price should not be null")
    @Min(value = 0, message="Price should not be less than 0")
    private Double price;

    @NotNull(message = "Stock should not be null")
    @Min(value = 0, message="Stock should not be less than 0")
    private Integer stock;

    @NotNull(message = "Weight should not be null")
    @Min(value = 0, message="Weight should not be less than 0")
    private Double weight;

}