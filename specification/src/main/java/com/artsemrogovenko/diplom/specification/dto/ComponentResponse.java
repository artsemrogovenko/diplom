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
public class ComponentResponse implements ComponentData {
    private Long id;
    private String factoryNumber;       // заводской номер
    private String model;       // модификация
    private String name;     // имя
    private Integer quantity;    // какое количество компонента в модуле
    private String unit;     // единица измерения
    private String description;  // тут можно указать например цвет
    private boolean refill;  // можно объединить?
    private Set<ModuleResponse> moduleResponses = new HashSet<>();
    @Override
    public boolean fieldsIsNull() {
        return factoryNumber==null && model==null && name==null  && quantity==null && unit==null && description==null && moduleResponses.isEmpty();
    }

}