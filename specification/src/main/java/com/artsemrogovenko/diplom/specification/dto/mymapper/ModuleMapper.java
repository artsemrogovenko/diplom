package com.artsemrogovenko.diplom.specification.dto.mymapper;

import com.artsemrogovenko.diplom.specification.dto.*;
import com.artsemrogovenko.diplom.specification.model.Component;
import com.artsemrogovenko.diplom.specification.model.Module;

import java.util.HashSet;
import java.util.Set;

public class ModuleMapper {


    public static <T extends ModuleData> Module mapToModule(T data) {
        Module module = null;
        if (data instanceof ModuleResponse) {
            module = new Module(((ModuleResponse) data).getId());
        } else {
            module = new Module();
        }

        if (data.getModel() != null) {
            module.setModel(data.getModel().trim());
        }
        if (data.getFactoryNumber() != null) {
            module.setFactoryNumber(data.getFactoryNumber().trim());
        }

        if (data.getName() != null) {
            module.setName(data.getName().trim());
        }
        if (data.getQuantity() != null) {
            module.setQuantity(data.getQuantity());
        }
        module.setUnit(data.getUnit().trim());
        if (data.getDescription() != null) {
            module.setDescription(data.getDescription().trim());
        }
        if (data.getCircutFile() != null) {
            module.setCircutFile(data.getCircutFile().trim());
        }
        // Копирование компонентов
        Set<Component> components = new HashSet<>();

        if (data instanceof ModuleRequest) {
            ModuleRequest moduleRequest = (ModuleRequest) data;
            if (moduleRequest.getComponentRequests() != null) {
                for (ComponentRequest componentRequest : moduleRequest.getComponentRequests()) {
                    Component component = ComponentMapper.mapToComponent(componentRequest);
                    components.add(component);
                }
            }
        }

        if (data instanceof ModuleResponse) {
            ModuleResponse moduleResponse = (ModuleResponse) data;
//            module.setId(moduleResponse.getId());
            for (ComponentResponse componentResponse : moduleResponse.getComponentResponses()) {
                Component component = ComponentMapper.mapToComponent(componentResponse);
                components.add(component);
            }
        }

        module.setComponents(components);
        return module;
    }

    public static ModuleResponse mapModuleToModuleResponse(Module module) {
        ModuleResponse moduleResponse = new ModuleResponse();

        moduleResponse.setId(module.getId());
        moduleResponse.setFactoryNumber(module.getFactoryNumber());
        moduleResponse.setModel(module.getModel());
        moduleResponse.setName(module.getName());
        moduleResponse.setQuantity(module.getQuantity());
        moduleResponse.setUnit(module.getUnit());
        moduleResponse.setDescription(module.getDescription());
        moduleResponse.setCircutFile(module.getCircutFile());
        // Копирование компонентов
        Set<ComponentResponse> componentResponses = new HashSet<>();
        for (Component component : module.getComponents()) {
            ComponentResponse componentRequest = ComponentMapper.mapToComponentResponse(component);
            componentResponses.add(componentRequest);
        }
        moduleResponse.setComponentResponses(componentResponses);
        return moduleResponse;
    }

}
