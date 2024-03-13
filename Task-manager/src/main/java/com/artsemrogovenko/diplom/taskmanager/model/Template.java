package com.artsemrogovenko.diplom.taskmanager.model;

import jakarta.persistence.*;
import  com.artsemrogovenko.diplom.taskmanager.model.Module;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@Entity
@NoArgsConstructor
public class Template implements TemplateData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;   //имя
    private String description; // описание
    @ManyToMany //один компонент может относится ко многим модулям
    private List<Module> modules = new ArrayList<>();
    public void addModule(Module module) {
        modules.add(module);
    }


}
