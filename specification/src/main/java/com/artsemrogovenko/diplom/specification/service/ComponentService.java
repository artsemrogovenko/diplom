package com.artsemrogovenko.diplom.specification.service;

import com.artsemrogovenko.diplom.specification.model.Component;
import com.artsemrogovenko.diplom.specification.repositories.ComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ComponentService {

    private final ComponentRepository componentRepository;

    public List<Component> getAllComponents() {
        return componentRepository.findAll();
    }

    public Component getComponentById(Long id) {
        return componentRepository.findById(id).orElseThrow(null);
    }

    public Component updateComponent(Component component) {
        Component componentById = getComponentById(component.getId());

        componentById.setFactoryNumber(component.getFactoryNumber());
        componentById.setName(component.getName());
        componentById.setModules(component.getModules());
        componentById.setQuantity(component.getQuantity());
        componentById.setUnit(component.getUnit());
        componentById.setDescription(component.getDescription());
        componentById.setModel(component.getModel());

        return componentRepository.save(componentById);
    }

    public Component createComponent(Component component) {
        return componentRepository.save(component);
    }

    public void deleteComponent(Long id) {
        Component componentById = getComponentById(id);
        componentRepository.delete(componentById);
    }

    public void saveAll(Set<Component> components) {
        if(!components.isEmpty())
        componentRepository.saveAll(components);
    }
}
