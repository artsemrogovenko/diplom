package com.artsemrogovenko.diplom.specification.dto.mymapper;

import com.artsemrogovenko.diplom.specification.dto.*;
import com.artsemrogovenko.diplom.specification.model.Component;
import com.artsemrogovenko.diplom.specification.model.Module;

import java.util.HashSet;
import java.util.Set;

public class ComponentMapper {

    public static <T extends ComponentData> Component mapToComponent(T componentData) {
        Component component = new Component();

        if (componentData.getFactoryNumber() != null) {
            component.setFactoryNumber(componentData.getFactoryNumber().trim());
        }
        if (componentData.getModel() != null) {
            component.setModel(componentData.getModel().trim());
        }
        if (componentData.getName() != null) {
            component.setName(componentData.getName().trim());
        }
        if (componentData.getQuantity() != null) {
            component.setQuantity(componentData.getQuantity());
        }
        if (componentData.getUnit() != null) {
            component.setUnit(componentData.getUnit().trim());
        }
        if (componentData.getDescription() != null) {
            component.setDescription(componentData.getDescription().trim());
        }

        // Копирование модулей
        Set<Module> modules = new HashSet<>();

//        if (componentData instanceof ComponentRequest) {
//            ComponentRequest moduleRequest = (ComponentRequest) componentData;
//            if (moduleRequest.getModuleRequests() != null ) {
//                for (ModuleRequest moduleReq : moduleRequest.getModuleRequests()) {
//                    modules.add(getModules(moduleReq));
//                }
//            }
//        }
        if (componentData instanceof ComponentResponse) {
            ComponentResponse moduleResponse = (ComponentResponse) componentData;
            component.setId(moduleResponse.getId());
            if (moduleResponse.getModuleResponses() != null) {
                for (ModuleResponse moduleResp : moduleResponse.getModuleResponses()) {
                    modules.add(getModules(moduleResp));
                }
            }
        }

        component.setModules(modules);
        return component;
    }

    private static <T extends ModuleData> Module getModules(T moduleData) {
        Module module = new Module();
        module.setFactoryNumber(moduleData.getFactoryNumber().trim());
        module.setModel(moduleData.getModel().trim());
        module.setName(moduleData.getName().trim());
        module.setQuantity(moduleData.getQuantity());
        module.setUnit(moduleData.getUnit().trim());
        module.setDescription(moduleData.getDescription().trim());
        // Копирование компонентов
        Set<Component> components = new HashSet<>();
//        if (moduleData instanceof ModuleRequest) {
//            ModuleRequest moduleRequest = (ModuleRequest) moduleData;
//            for (ComponentRequest componentRequest : moduleRequest.getComponentRequests()) {
//                Component component = mapToComponent(componentRequest);
//                components.add(component);
//            }
//        }
        if (moduleData instanceof ModuleResponse) {
            ModuleResponse moduleResponse = (ModuleResponse) moduleData;
            module.setId(moduleResponse.getId());
            for (ComponentResponse componentResponse : moduleResponse.getComponentResponses()) {
                Component component = mapToComponent(componentResponse);
                components.add(component);
            }
        }

        module.setComponents(components);
        return module;
    }

    public static ComponentResponse mapToComponentResponse(Component component) {
        ComponentResponse componentResponse = new ComponentResponse();

        componentResponse.setId(component.getId());
        componentResponse.setFactoryNumber(component.getFactoryNumber());
        componentResponse.setModel(component.getModel());
        componentResponse.setName(component.getName());
        componentResponse.setQuantity(component.getQuantity());
        componentResponse.setUnit(component.getUnit());
        componentResponse.setDescription(component.getDescription());
        // Копирование модулей
        Set<ModuleResponse> moduleResponses = new HashSet<>();
        for (Module module : component.getModules()) {
            ModuleResponse moduleResponse = new ModuleResponse();
            moduleResponse.setFactoryNumber(module.getFactoryNumber());
            moduleResponse.setModel(module.getModel());
            moduleResponse.setName(module.getName());
            moduleResponse.setQuantity(module.getQuantity());
            moduleResponse.setUnit(module.getUnit());
            moduleResponse.setDescription(module.getDescription());
            // Копирование компонентов
            Set<ComponentResponse> components = new HashSet<>();
            for (Component c : module.getComponents()) {
                ComponentResponse subComponentResponse = mapToComponentResponse(c);
                components.add(subComponentResponse);
            }
            moduleResponse.setComponentResponses(components);
            moduleResponses.add(moduleResponse);
        }
        componentResponse.setModuleResponses(moduleResponses);
        return componentResponse;
    }


}
