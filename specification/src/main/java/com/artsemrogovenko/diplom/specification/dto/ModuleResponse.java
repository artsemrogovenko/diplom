package com.artsemrogovenko.diplom.specification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleResponse implements ModuleData{
    private Long id;
    private String factoryNumber;       // заводской номер
    private String model;       // модификация
    private String name;     // имя
    private int quantity;    // какое количество компонента в модуле
    private String unit;     // единица измерения
    private String description;  // тут можно указать например цвет

    //один модуль может содержать несколько компонентов
    private Set<ComponentResponse> componentResponses = new HashSet<>();   // список компонентов

    public void addComponent(ComponentResponse c) {
        componentResponses.add(c);
    }
}
