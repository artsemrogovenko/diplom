package com.artsemrogovenko.diplom.taskmanager.repository;


import com.artsemrogovenko.diplom.taskmanager.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

Module findFirstByFactoryNumberAndModelAndNameAndQuantityAndUnitAndDescriptionAndCircuitFile(String factoryNumber, String model, String name, Integer quantity, String unit, String description, String circutFile);

  @Query("SELECT e FROM Module e WHERE e.id = (SELECT MAX(ee.id) FROM Module ee)")
  Module findLastModule();


}
