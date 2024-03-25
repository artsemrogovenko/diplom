package com.artsemrogovenko.diplom.taskmanager.repository;


import com.artsemrogovenko.diplom.taskmanager.model.Component;
import com.artsemrogovenko.diplom.taskmanager.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.artsemrogovenko.diplom.taskmanager.model.Module;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

Module findFirstByFactoryNumberAndModelAndNameAndQuantityAndUnitAndDescriptionAndCircutFile(String factoryNumber, String model, String name, Integer quantity,String unit, String description, String circutFile);

  @Query("SELECT e FROM Module e WHERE e.id = (SELECT MAX(ee.id) FROM Module ee)")
  Module findLastModule();


}
