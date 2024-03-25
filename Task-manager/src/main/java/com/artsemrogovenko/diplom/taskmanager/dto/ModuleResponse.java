package com.artsemrogovenko.diplom.taskmanager.dto;

import com.artsemrogovenko.diplom.taskmanager.model.MyCollection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleResponse implements ModuleData, SavedModule {
    private Long id;
    private String factoryNumber;       // заводской номер
    private String model;       // модификация
    private String name;     // имя
    private Integer quantity;    // какое количество
    private String unit;     // единица измерения
    private String description;  // тут можно указать например цвет
    private MyCollection<ComponentResponse> componentResponses;   // список компонентов
    private String circutFile;

    public void addComponent(ComponentResponse c) {
        componentResponses.add(c);
    }

    @Override
    public boolean fieldsIsNull() {
        return factoryNumber == null && model == null && name == null && quantity == null && unit == null && description == null && componentResponses.isEmpty() && circutFile == null;
    }
}
