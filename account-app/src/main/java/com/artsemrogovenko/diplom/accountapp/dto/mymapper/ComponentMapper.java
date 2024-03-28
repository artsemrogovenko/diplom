package com.artsemrogovenko.diplom.accountapp.dto.mymapper;

import com.artsemrogovenko.diplom.accountapp.dto.ComponentData;
import com.artsemrogovenko.diplom.accountapp.models.Component;
import com.artsemrogovenko.diplom.accountapp.models.ComponentRequest;

public class ComponentMapper {

    public static <T extends ComponentData> Component mapToComponent(T componentData) {
        Component component;

//        if (componentData instanceof Component) {
//            component = new Component(((Component) componentData).getId());
//        } else {
            component = new Component();
//        }


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

        if (componentData instanceof Component) {
            component.setId(((Component) componentData).getId());
        }

        return component;
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
