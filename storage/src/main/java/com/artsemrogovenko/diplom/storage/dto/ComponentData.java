package com.artsemrogovenko.diplom.storage.dto;

public interface ComponentData {
    String getFactoryNumber();
    String getModel();
    String getName();
    int getQuantity();
    String getUnit();
    String getDescription();
    boolean isRefill();
}
