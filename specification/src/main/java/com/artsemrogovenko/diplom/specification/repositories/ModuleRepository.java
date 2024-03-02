package com.artsemrogovenko.diplom.specification.repositories;


import com.artsemrogovenko.diplom.specification.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
}
