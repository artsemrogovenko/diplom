package com.artsemrogovenko.diplom.specification.repositories;


import com.artsemrogovenko.diplom.specification.model.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {


  Optional<Component>  findByFactoryNumberAndModelAndNameAndDescription(String factoryNumber, String model, String name, String description);
}
