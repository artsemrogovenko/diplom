package com.artsemrogovenko.diplom.storage.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.artsemrogovenko.diplom.storage.model.Deficit;

import java.util.Optional;

@Repository
public interface DeficitRepository extends JpaRepository<Deficit, Long> {

    Optional<Deficit> findByFactoryNumberAndModelAndNameAndUnitAndDescriptionAndRefill(
            String factoryNumber, String model, String name, String unit, String description, boolean refill);

}
