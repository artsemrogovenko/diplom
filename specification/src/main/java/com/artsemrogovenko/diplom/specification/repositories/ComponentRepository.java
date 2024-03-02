package com.artsemrogovenko.diplom.specification.repositories;


import com.artsemrogovenko.diplom.specification.model.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {
}
