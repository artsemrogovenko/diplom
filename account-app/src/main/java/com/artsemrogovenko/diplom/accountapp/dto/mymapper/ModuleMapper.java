package com.artsemrogovenko.diplom.accountapp.dto.mymapper;

import com.artsemrogovenko.diplom.accountapp.models.Module;

public class ModuleMapper {


    public static Module mapToModule(Module data) {
        Module module = new Module(data.getId());
//        Module module = new Module();

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
        if (data.getUnit() != null) {
            module.setUnit(data.getUnit().trim());
        }
        if (data.getDescription() != null) {
            module.setDescription(data.getDescription().trim());
        }
        if (data.getCircuitFile() != null) {
            module.setCircuitFile(data.getCircuitFile().trim());
        }


        if (data.getComponents() != null && !data.getComponents().isEmpty()) {
            module.setComponents(data.getComponents());
        }

        return module;
    }


}
