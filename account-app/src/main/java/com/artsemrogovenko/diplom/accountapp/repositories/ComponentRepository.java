package com.artsemrogovenko.diplom.accountapp.repositories;


import com.artsemrogovenko.diplom.accountapp.models.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {
  Optional<Component>  findByFactoryNumberAndModelAndNameAndUnitAndDescription(String factoryNumber, String model, String name,String unit,String description);


  List<Component> findAllByModulesId(Long moduleId);

  Component  findFirstByFactoryNumberAndModelAndNameAndQuantityAndUnitAndDescription(String factoryNumber, String model, String name, Integer quantity, String unit, String description);
}
