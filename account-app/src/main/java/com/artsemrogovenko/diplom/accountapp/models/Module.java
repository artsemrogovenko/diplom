package com.artsemrogovenko.diplom.accountapp.models;

import com.artsemrogovenko.diplom.accountapp.dto.ModuleData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
public class Module  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //

    @Column(columnDefinition = "VARCHAR(100)")
    private String factoryNumber;       // заводской номер

    @Column(columnDefinition = "VARCHAR(100)")
    private String model;       // модификация
    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String name;     // имя

    @Column(columnDefinition = "INT", nullable = true)
    private Integer quantity;    // какое количество

    @Column(columnDefinition = "VARCHAR(20)")
    private String unit;     // единица измерения

    @Column(columnDefinition = "VARCHAR(255)")
    private String description;  // тут можно указать например цвет
    private String circutFile;  // схема сборки

    //один модуль может содержать несколько компонентов
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "module_components", joinColumns = @JoinColumn(name = "module_id"), inverseJoinColumns = @JoinColumn(name = "component_id"))
    private Set<Component> components = new HashSet<>();    // список компонентов
//    @ManyToMany(mappedBy = "modules",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
//    @JoinTable(name = "module_tasks", joinColumns = @JoinColumn(name = "module_id"), inverseJoinColumns = @JoinColumn(name = "task_id"))
//    private List<Task> tasks = new ArrayList<>();
@JsonIgnore
@ManyToMany(mappedBy = "modules", fetch = FetchType.LAZY)
private List<Task> tasks = new ArrayList<>();


    public Module(Long id) {
        this.id = id;
    }

    public void addComponent(Component newComponent) {
        components.add(newComponent);
    }


}
