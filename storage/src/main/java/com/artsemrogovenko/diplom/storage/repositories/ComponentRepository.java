package com.artsemrogovenko.diplom.storage.repositories;

import com.artsemrogovenko.diplom.storage.model.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {
    List<Component> findAllByName(String name);
//    Optional<List<Component>> findByFactoryNumberAndModelAndNameAndUnitAndDescription(String factoryNumber, String model, String name, String unit, String description);

    @Query("SELECT c FROM Component c WHERE c.factoryNumber = :factoryNumber " +
            "AND c.model = :model AND c.name = :name AND c.unit = :unit AND c.description = :description")
  List<Component> findAllByFactoryNumberAndModelAndNameAndUnitAndDescription(String factoryNumber, String model, String name, String unit, String description);

}
