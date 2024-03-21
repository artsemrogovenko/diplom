package com.artsemrogovenko.diplom.accountapp.repositories;


import com.artsemrogovenko.diplom.accountapp.models.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    Optional<Module> findByFactoryNumberAndModelAndNameAndUnitAndDescriptionAndCircutFile(String factoryNumber, String model, String name, String unit, String description, String circutFile);

    @Query("SELECT e FROM Module e WHERE e.id = (SELECT MAX(ee.id) FROM Module ee)")
    Module findLastModule();

   Optional<Module>  findDistinctFirstByFactoryNumberAndModelAndNameAndUnitAndDescriptionAndCircutFile(String factoryNumber, String model, String name, String unit, String description,String circuit);

    List<Module> findAllByTasks_Id(Long id);
}
