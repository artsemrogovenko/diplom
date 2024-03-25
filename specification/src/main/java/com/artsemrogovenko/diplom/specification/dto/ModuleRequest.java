package com.artsemrogovenko.diplom.specification.dto;

import com.artsemrogovenko.diplom.specification.model.MyCollection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@Scope("request")
public class ModuleRequest implements ModuleData {
    private String factoryNumber;       // заводской номер
    private String model;       // модификация
    private String name;     // имя
    private Integer quantity;    // какое количество
    private String unit;     // единица измерения
    private String description;  // тут можно указать например цвет

    //один модуль может содержать несколько компонентов
    private MyCollection componentRequests=new MyCollection();
    private String circuitVersion;  // схема сборки

    public void addComponent(ComponentRequest c) {
        componentRequests.add(c);
    }

    @Override
    public boolean fieldsIsNull() {
        return factoryNumber == null && model == null && name == null && quantity == null && unit == null && description == null && componentRequests == null && circuitVersion == null;
    }

    @Override
    public String getCircuitFile() {
        return circuitVersion;
    }

}
