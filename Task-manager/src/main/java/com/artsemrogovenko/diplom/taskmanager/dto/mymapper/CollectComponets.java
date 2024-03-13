package com.artsemrogovenko.diplom.taskmanager.dto.mymapper;

import com.artsemrogovenko.diplom.taskmanager.model.Component;
import com.artsemrogovenko.diplom.taskmanager.model.Task;

import java.util.List;

public class CollectComponets {
    public static List<Component> calculate(Task task) {
        List<Component> components = task.getModules().stream()
                .flatMap(module -> module.getComponents().stream())
                .toList();
        return components;
    }
}
