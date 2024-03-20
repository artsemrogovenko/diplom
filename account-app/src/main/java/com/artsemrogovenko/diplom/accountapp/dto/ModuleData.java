package com.artsemrogovenko.diplom.accountapp.dto;

import com.artsemrogovenko.diplom.accountapp.models.Component;

import java.util.Set;

public interface ModuleData {

    String getFactoryNumber();

    String getModel();

    String getName();

    Integer getQuantity();

    String getUnit();

    String getDescription();

    String getCircutFile();

//    boolean fieldsIsNull();

    Set<Component> getComponents();
}
