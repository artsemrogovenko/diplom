package com.artsemrogovenko.diplom.accountapp.models;


public class ComponentMapper {

    public static  ComponentRequest mapToComponentRequest(Component component) {
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
