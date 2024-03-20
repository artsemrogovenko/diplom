package com.artsemrogovenko.diplom.accountapp.models;

import com.artsemrogovenko.diplom.accountapp.dto.ComponentData;
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


    public boolean fieldsIsNull() {
        return factoryNumber==null && model==null && name==null  && quantity==null && unit==null && description==null;
    }



}