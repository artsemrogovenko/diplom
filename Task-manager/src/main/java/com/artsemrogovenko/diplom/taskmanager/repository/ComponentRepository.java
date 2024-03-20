package com.artsemrogovenko.diplom.taskmanager.repository;


import com.artsemrogovenko.diplom.taskmanager.model.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {

  Optional<Component>  findByFactoryNumberAndModelAndNameAndUnitAndDescription(String factoryNumber, String model, String name,String unit,String description);

  Optional<Component>  findByFactoryNumberAndModelAndNameAndDescription(String factoryNumber, String model, String name, String description);

//
//
//  @Query(nativeQuery = true, value = "SELECT * FROM COMPONENT " +
//          "WHERE " +
//          "(FACTORY_NUMBER = :factoryNumber OR :factoryNumber IS NULL) AND " +
//          "(MODEL = :model OR :model IS NULL) AND " +
//          "(NAME = :name OR :name IS NULL) AND " +
//          "(UNIT = :unit OR :unit IS NULL) AND " +
//          "(DESCRIPTION = :description OR :description IS NULL)")
//  List<Component> findByFactoryNumberAndModelAndNameAndUnitAndDescription(@Param("factoryNumber") String factoryNumber,
//                                                                          @Param("model") String model,
//                                                                          @Param("name") String name,
//                                                                          @Param("unit") String unit,
//                                                                          @Param("description") String description);

}
