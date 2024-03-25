package com.artsemrogovenko.diplom.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentRequest implements ComponentData {
    private String factoryNumber;       // заводской номер
    private String model;       // модификация
    private String name;     // имя
    private Integer quantity;    // какое количество компонента в модуле
    private String unit;     // единица измерения
    private String description;  // тут можно указать например цвет

    @Override
    public boolean fieldsIsNull() {
        return factoryNumber==null && model==null && name==null  && quantity==null && unit==null && description==null;
    }


}