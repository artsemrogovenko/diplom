package com.artsemrogovenko.diplom.storage.dto.mymapper;

import com.artsemrogovenko.diplom.storage.dto.ComponentData;
import com.artsemrogovenko.diplom.storage.dto.ComponentRequest;
import com.artsemrogovenko.diplom.storage.dto.ComponentResponse;
import com.artsemrogovenko.diplom.storage.model.Component;

public class ComponentMapper {

    public static <T extends ComponentData> Component mapToComponent(T componentData) {
        Component component = new Component();

        if (componentData.getFactoryNumber() != null) {
            if (componentData.getFactoryNumber().trim() == "") {
                component.setFactoryNumber(null);
            } else {
                component.setFactoryNumber(componentData.getFactoryNumber().trim());
            }
        }
        if (componentData.getModel() != null) {
            if (componentData.getModel().trim() == "") {
                component.setModel(null);
            } else {
                component.setModel(componentData.getModel().trim());
            }
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
            if (componentData.getDescription().trim() == "") {
                component.setDescription(null);
            } else {
                component.setDescription(componentData.getDescription().trim());
            }
        }

        if (componentData instanceof ComponentResponse) {
            ComponentResponse moduleResponse = (ComponentResponse) componentData;
            if (moduleResponse.getRefill() != null) {
                component.setRefill(moduleResponse.getRefill().booleanValue());
            }
            component.setId(moduleResponse.getId());
        }

        if (componentData instanceof ComponentRequest) {
            component.setRefill(((ComponentRequest) componentData).getRefill().booleanValue());
        }

        return component;
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
        componentResponse.setRefill(component.getRefill());

        return componentResponse;
    }


}
