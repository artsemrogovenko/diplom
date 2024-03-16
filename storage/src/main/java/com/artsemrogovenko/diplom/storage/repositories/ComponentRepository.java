package com.artsemrogovenko.diplom.storage.repositories;

import com.artsemrogovenko.diplom.storage.model.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {
    List<Component> findAllByName(String name);
    Optional<List<Component>> findByFactoryNumberAndModelAndNameAndUnitAndDescription(String factoryNumber, String model, String name, String unit, String description);
}
