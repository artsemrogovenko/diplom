package com.artsemrogovenko.diplom.taskmanager.dto;

public interface ComponentData {

    String getFactoryNumber();

    String getModel();

    String getName();

    Integer getQuantity();

    String getUnit();

    String getDescription();
    boolean fieldsIsNull();

}
