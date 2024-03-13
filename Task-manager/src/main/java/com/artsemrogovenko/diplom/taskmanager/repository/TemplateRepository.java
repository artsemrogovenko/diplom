package com.artsemrogovenko.diplom.taskmanager.repository;

import com.artsemrogovenko.diplom.taskmanager.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateRepository extends JpaRepository<Template,Long> {
    @Query("SELECT t.modules FROM Template t WHERE t.id = :templateId")
    List<Module> findModulesByTemplateId(@Param("templateId") Long templateId);
}
