package com.artsemrogovenko.diplom.diagrams.repository;

import com.artsemrogovenko.diplom.diagrams.model.SchemeForModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
public interface SchemeFileRepository extends JpaRepository<SchemeForModule,Long> {
    Optional<List<SchemeForModule>> findByModuleNameAndModificationAndVersionAssembly(String name, String modification, String version);
    @Query("SELECT DISTINCT s.moduleName FROM SchemeForModule s")
    List<String> findAllModuleNames();

    List<SchemeForModule> findAllByModuleName(String name);
}
