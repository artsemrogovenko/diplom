package com.artsemrogovenko.diplom.taskmanager.repository;

import com.artsemrogovenko.diplom.taskmanager.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,String > {
}
