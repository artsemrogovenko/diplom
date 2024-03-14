package com.artsemrogovenko.diplom.taskmanager.model;

import com.artsemrogovenko.diplom.taskmanager.dto.ComponentData;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
public class Component implements ComponentData {
    @Id
    private Long id; // этот компонент наследован из сервиса модулей, и не требует авто генерации
    @ManyToMany //один компонент может относится ко многим модулям
    private Set<Module> modules = new HashSet<>();
    @Column(columnDefinition = "VARCHAR(100)")
    private String factoryNumber;       // заводской номер
    @Column(columnDefinition = "VARCHAR(100)")
    private String model;       // модификация
    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String name;     // имя
    @Column(columnDefinition = "INT", nullable = false)
    private Integer quantity;    // какое количество компонента в модуле
    @Column(columnDefinition = "VARCHAR(20)", nullable = false)
    private String unit;     // единица измерения
    @Column(columnDefinition = "VARCHAR(255)")
    private String description;  // тут можно указать например цвет

    public boolean fieldsIsNull() {
        return id == null && factoryNumber == null && model == null && name == null && quantity == null && unit == null && description == null ;
    }

}