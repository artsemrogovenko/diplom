package com.artsemrogovenko.diplom.accountapp.repositories;


import com.artsemrogovenko.diplom.accountapp.models.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {
  Optional<Component>  findByFactoryNumberAndModelAndNameAndDescription(String factoryNumber, String model, String name, String description);
}
