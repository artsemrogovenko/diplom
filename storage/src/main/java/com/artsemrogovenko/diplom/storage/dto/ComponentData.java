package com.artsemrogovenko.diplom.storage.dto;

public interface ComponentData {
    String getFactoryNumber();
    String getModel();
    String getName();
    Integer getQuantity();
    String getUnit();
    String getDescription();
    Boolean getRefill();
}
