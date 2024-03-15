package com.artsemrogovenko.diplom.accountapp.services;

import com.artsemrogovenko.diplom.accountapp.models.Component;
import com.artsemrogovenko.diplom.accountapp.repositories.ComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ComponentService {

    private final ComponentRepository componentRepository;

    public List<Component> getAllComponents() {
        return componentRepository.findAll();
    }

    public Component getComponentById(Long id) {
        return componentRepository.findById(id).get();
    }

    public Component createComponent(Component component) {
        if (!component.fieldsIsNull()) {
            if (!componentRepository.equals(component)) {
                return componentRepository.save(component);
            }
        }
        return new Component();
    }

    public void deleteComponent(Long id) {
        componentRepository.deleteById(id);
    }

    public List<Component> saveAll(Set<Component> components) {
        if (components != null && !components.isEmpty()) {
            List<Component> nonDuplicates = components.stream()
                    .filter(component -> !component.fieldsIsNull())
                    .filter(component -> notExist(component)).toList();
            nonDuplicates.size();
            // Сохранить все отфильтрованные компоненты
            return componentRepository.saveAll(nonDuplicates);
        }
        return null;
    }

    public boolean notExist(Component component) {
        String factoryNumber = component.getFactoryNumber();
        String model = component.getModel();
        String name = component.getName();
//        String unit = component.getUnit();
        String description = component.getDescription();
        try {
            Component existingComponent = componentRepository.findByFactoryNumberAndModelAndNameAndDescription(factoryNumber, model, name, description).get();
        } catch (NoSuchElementException e) {
//            System.out.println("no element");
            return true;
        }
        return false;
    }
}
