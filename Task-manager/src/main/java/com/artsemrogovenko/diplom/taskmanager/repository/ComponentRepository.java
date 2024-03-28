package com.artsemrogovenko.diplom.taskmanager.repository;


import com.artsemrogovenko.diplom.taskmanager.model.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {

    Component findFirstByFactoryNumberAndModelAndNameAndUnitAndDescription(String factoryNumber, String model, String name, String unit, String description);

}
