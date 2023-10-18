package com.gfttraining.productAPI.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductResponse {
    @NotNull(message = "Id should not be null")
    private long id;

    @NotNull(message = "Price should not be null")
    @Min(value = 0, message="Price should not be less than 0")
    private BigDecimal price;

    @NotNull(message = "Stock should not be null")
    @Min(value = 0, message="Stock should not be less than 0")
    private int stock;

    @NotNull(message = "Weight should not be null")
    @Min(value = 0, message="Weight should not be less than 0")
    private Double weight;

    public ProductResponse(Product product){
        double priceNotRounded = (1 - product.getCategory().getDiscount()/100) * product.getPrice();
        BigDecimal bd = new BigDecimal(priceNotRounded);
        BigDecimal roundedPrice = bd.setScale(2, RoundingMode.CEILING);

        this.id = product.getId();
        this.price = roundedPrice;
        this.stock = product.getStock();
        this.weight = product.getWeight();
    }
    
}
