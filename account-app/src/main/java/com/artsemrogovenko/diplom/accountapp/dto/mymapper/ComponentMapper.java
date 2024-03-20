package com.artsemrogovenko.diplom.accountapp.dto.mymapper;

import com.artsemrogovenko.diplom.accountapp.models.Module;

import com.artsemrogovenko.diplom.accountapp.dto.ComponentData;
import com.artsemrogovenko.diplom.accountapp.dto.ModuleData;
import com.artsemrogovenko.diplom.accountapp.models.Component;
import com.artsemrogovenko.diplom.accountapp.models.ComponentRequest;

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

//        // Копирование модулей
//        Set<Module> modules = new HashSet<>();
//        if (componentData instanceof Component) {
//            component.setId(((Component) componentData).getId());
//        }
//        if (componentData instanceof ComponentRequest) {
//            ComponentRequest moduleRequest = (ComponentRequest) componentData;
//            if (moduleRequest.getModuleRequests() != null ) {
//                for (ModuleRequest moduleReq : moduleRequest.getModuleRequests()) {
//                    modules.add(getModules(moduleReq));
//                }
//            }
//        }


//        component.setModules(modules);
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
//        Set<Component> components = new HashSet<>();

//        if (moduleData instanceof ModuleRequest) {
//            ModuleRequest moduleRequest = (ModuleRequest) moduleData;
//            for (ComponentRequest componentRequest : moduleRequest.getComponentRequests()) {
//                Component component = mapToComponent(componentRequest);
//                components.add(component);
//            }
//        }


//        module.setComponents(components);
        return module;
    }


    public static <T extends ComponentData> ComponentRequest mapToComponentRequest(T component) {
        ComponentRequest componentRequest = new ComponentRequest();

        componentRequest.setFactoryNumber(component.getFactoryNumber());
        componentRequest.setModel(component.getModel());
        componentRequest.setName(component.getName());
        componentRequest.setQuantity(component.getQuantity());
        componentRequest.setUnit(component.getUnit());
        componentRequest.setDescription(component.getDescription());

        return componentRequest;
    }


}
