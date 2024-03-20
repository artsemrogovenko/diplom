package com.artsemrogovenko.diplom.specification.repositories;


import com.artsemrogovenko.diplom.specification.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

  Optional<Module>  findByFactoryNumberAndModelAndNameAndUnitAndDescription(String factoryNumber, String model, String name, String unit, String description);
}
