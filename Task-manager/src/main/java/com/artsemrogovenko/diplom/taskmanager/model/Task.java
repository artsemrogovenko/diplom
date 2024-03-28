package com.artsemrogovenko.diplom.taskmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

//    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinTable(
//            name = "task_modules", // Имя таблицы для связи между Module и Task
//            joinColumns = @JoinColumn(name = "module_id"), // Столбец для связи с Module
//            inverseJoinColumns = @JoinColumn(name = "task_id") // Столбец для связи с Task
//    )
    @ManyToMany
    private List<Module> modules = new ArrayList<>();
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

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
