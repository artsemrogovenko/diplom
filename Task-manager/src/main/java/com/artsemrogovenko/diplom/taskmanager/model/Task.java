package com.artsemrogovenko.diplom.taskmanager.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Task implements TemplateData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;   //имя
    private String description; // описание
    private TaskStatus status;
    private String contractNumber; // номер договора
    private String owner; // у кого сейчас задача
    private boolean reserved;
    @ManyToMany // список модулей
    private List<Module> modules = new ArrayList<>();

    public Task(Template template) {
        this.modules = template.getModules();
    }

    public boolean fieldNotEmpty() {
        return name != null && description != null && contractNumber != null;
    }

    public void addModule(Module module) {
        modules.add(module);
    }

    // сработает при создании экземпляра сущности
    // перед сохранением этой сущности в базу данных.
    @PrePersist
    void onCreate() {
        if (this.status == null) {
            this.status = status.TO_DO;
        }
        if (this.owner == null) {
            this.owner = "kanban";
        }
    }

}
