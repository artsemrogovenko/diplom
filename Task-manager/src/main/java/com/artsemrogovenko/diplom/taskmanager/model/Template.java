package com.artsemrogovenko.diplom.taskmanager.model;

import jakarta.persistence.*;
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
    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    private List<Module> modules = new ArrayList<>();
    public void addModule(Module module) {
        modules.add(module);
    }


}
