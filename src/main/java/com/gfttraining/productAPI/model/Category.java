package com.gfttraining.productAPI.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @NotBlank(message = "The category name cannot be blank")
    private String name;

    @NotNull(message = "The discount cannot be null")
    @Min(value = 0, message ="Discount can't be less than 0%")
    @Max(value = 100, message= "Discount can't be more than 100%")
    private double discount;

}

