package com.artsemrogovenko.diplom.specification.model;

import com.artsemrogovenko.diplom.specification.dto.ModuleData;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
public class Module implements ModuleData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    //один модуль может содержать несколько компонентов
    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.DETACH, CascadeType.MERGE,CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "module_components", joinColumns = @JoinColumn(name = "module_id"), inverseJoinColumns = @JoinColumn(name = "component_id"))
    private Set<Component> components = new HashSet<>();   // список компонентов
    private String circuitFile;  // схема сборки

    // Конструктор для установки идентификатора вручную
    public Module(Long id) {
        this.id = id;
    }

    public void addComponent(Component c) {
        components.add(c);
    }

    public boolean fieldsIsNull() {
        return id == null && factoryNumber == null && model == null && name == null && quantity == null && unit == null && description == null && components.isEmpty() && circuitFile == null;
    }
}
