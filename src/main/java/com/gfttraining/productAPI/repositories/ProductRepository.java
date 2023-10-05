package com.gfttraining.productAPI.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gfttraining.productAPI.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer>{
    
}
