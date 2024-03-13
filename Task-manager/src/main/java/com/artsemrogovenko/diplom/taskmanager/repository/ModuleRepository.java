package com.artsemrogovenko.diplom.taskmanager.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.artsemrogovenko.diplom.taskmanager.model.Module;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
  Optional<Module>  findByFactoryNumberAndModelAndNameAndUnitAndDescription(String factoryNumber, String model, String name, String unit, String description);

//  List<Module> findByTemplateId(Long entityId);
}
