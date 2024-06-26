package com.artsemrogovenko.diplom.diagrams.repository;

import com.artsemrogovenko.diplom.diagrams.model.SchemeForModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SchemeFileRepository extends JpaRepository<SchemeForModule, Long> {
    Optional<List<SchemeForModule>> findByModuleNameAndModificationAndVersionAssembly(String name, String modification, String version);

    @Query("SELECT s.filePath FROM SchemeForModule s WHERE s.moduleName = :moduleName AND s.modification = :modification AND s.versionAssembly = :versionAssembly")
    Optional<String> findFilePathByModuleNameAndModificationAndVersionAssembly(
            @Param("moduleName") String moduleName, @Param("modification") String modification, @Param("versionAssembly") String versionAssembly);

    @Query("SELECT DISTINCT s.moduleName FROM SchemeForModule s")
    List<String> findAllModuleNames();

    List<SchemeForModule> findAllByModuleName(String name);
}
