package com.artsemrogovenko.diplom.accountapp.dto.mymapper;

import com.artsemrogovenko.diplom.accountapp.dto.ModuleData;
import com.artsemrogovenko.diplom.accountapp.models.Component;
import com.artsemrogovenko.diplom.accountapp.models.Module;

import java.util.HashSet;
import java.util.Set;

public class ModuleMapper {


    public static Module mapToModule(Module data) {
        Module module = new Module();
        if (data.getFactoryNumber() != null) {
            module.setFactoryNumber(data.getFactoryNumber().trim());
        }
        if (data.getModel() != null) {
            module.setModel(data.getModel().trim());
        }
        if (data.getName() != null) {
            module.setName(data.getName().trim());
        }
        if (data.getQuantity() != null) {
            module.setQuantity(data.getQuantity());
        }
        if (module.getUnit() != null) {
            module.setUnit(data.getUnit().trim());
        }
        if (data.getDescription() != null) {
            module.setDescription(data.getDescription().trim());
        }
        if (data.getCircutFile() != null) {
            module.setCircutFile(data.getCircutFile().trim());
        }
        // Копирование компонентов
//        Set<Component> components = new HashSet<>();
//
        if (data.getComponents() != null && !data.getComponents().isEmpty()) {
            module.setComponents(data.getComponents());
        }
//
        return module;
    }


}
