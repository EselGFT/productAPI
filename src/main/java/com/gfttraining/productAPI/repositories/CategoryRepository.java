package com.gfttraining.productAPI.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gfttraining.productAPI.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category,String> {
    
}
