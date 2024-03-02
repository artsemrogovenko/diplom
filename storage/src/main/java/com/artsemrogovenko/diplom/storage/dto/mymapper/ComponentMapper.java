package com.artsemrogovenko.diplom.storage.dto.mymapper;

import com.artsemrogovenko.diplom.storage.dto.ComponentData;
import com.artsemrogovenko.diplom.storage.dto.ComponentResponse;
import com.artsemrogovenko.diplom.storage.model.Component;

public class ComponentMapper {

    public static <T extends ComponentData> Component mapToComponent(T componentData) {
        Component component = new Component();

        component.setFactoryNumber(componentData.getFactoryNumber());
        component.setModel(componentData.getModel());
        component.setName(componentData.getName());
        component.setQuantity(componentData.getQuantity());
        component.setUnit(componentData.getUnit());
        component.setDescription(componentData.getDescription());
        component.setRefill(componentData.isRefill());

        if (componentData instanceof ComponentResponse) {
            ComponentResponse moduleResponse = (ComponentResponse) componentData;
            component.setId(moduleResponse.getId());
        }

        return component;
    }


    public static  ComponentResponse mapToComponentResponse(Component component) {
        ComponentResponse componentResponse = new ComponentResponse();

        componentResponse.setId(component.getId());
        componentResponse.setFactoryNumber(component.getFactoryNumber());
        componentResponse.setModel(component.getModel());
        componentResponse.setName(component.getName());
        componentResponse.setQuantity(component.getQuantity());
        componentResponse.setUnit(component.getUnit());
        componentResponse.setDescription(component.getDescription());
        componentResponse.setRefill(component.isRefill());

        return componentResponse;
    }


}
