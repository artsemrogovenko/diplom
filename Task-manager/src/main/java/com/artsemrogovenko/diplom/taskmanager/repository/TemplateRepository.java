package com.artsemrogovenko.diplom.taskmanager.repository;

import com.artsemrogovenko.diplom.taskmanager.model.Module;
import com.artsemrogovenko.diplom.taskmanager.model.Task;
import com.artsemrogovenko.diplom.taskmanager.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template,Long> {

    Optional<Template> findByNameAndDescription(String name, String description);
    @Query("SELECT t.modules FROM Template t WHERE t.id = :templateId")
    List<Module> findModulesByTemplateId(@Param("templateId") Long templateId);

    @Query("SELECT e FROM Template e ORDER BY e.id DESC")
    Template findLastTemplate();
}
