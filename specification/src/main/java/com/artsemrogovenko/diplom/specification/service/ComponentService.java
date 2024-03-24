package com.artsemrogovenko.diplom.specification.service;

import com.artsemrogovenko.diplom.specification.dto.ComponentRequest;
import com.artsemrogovenko.diplom.specification.dto.ComponentResponse;
import com.artsemrogovenko.diplom.specification.dto.mymapper.ComponentMapper;
import com.artsemrogovenko.diplom.specification.model.Component;
import com.artsemrogovenko.diplom.specification.repositories.ComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ComponentService {

    private final ComponentRepository componentRepository;

    public List<ComponentResponse> getAllComponents() {
        return componentRepository.findAll().stream().map(ComponentMapper::mapToComponentResponse).toList();
    }

    public ComponentResponse getComponentById(Long id) {
        return ComponentMapper.mapToComponentResponse(componentRepository.findById(id).get());
    }

    public ComponentResponse updateComponent(ComponentResponse componentResponse) {
        Component componentById = componentRepository.findById(componentResponse.getId()).get();

        componentById = ComponentMapper.mapToComponent(componentResponse);

//        componentById.setFactoryNumber(componentResponse.getFactoryNumber());
//        componentById.setName(componentResponse.getName());
//        componentById.setModules(componentResponse.getModuleResponses().stream().  map(moduleResponse -> ComponentMapper.mapToComponent(moduleResponse)).toList());
//        componentById.setQuantity(componentResponse.getQuantity());
//        componentById.setUnit(componentResponse.getUnit());
//        componentById.setDescription(componentResponse.getDescription());
//        componentById.setModel(componentResponse.getModel());

//                componentRepository.save(componentById);
        return ComponentMapper.mapToComponentResponse(componentRepository.save(componentById));
    }

    public ComponentResponse createComponent(ComponentRequest componentRequest) {
        if (!componentRequest.fieldsIsNull()) {
            Component temp = ComponentMapper.mapToComponent(componentRequest);
            if (!componentRepository.equals(temp)) {
                return ComponentMapper.mapToComponentResponse(componentRepository.save(temp));
            }
        }
        return new ComponentResponse();
    }

    public void deleteComponent(Long id) {
//        Component componentById = getComponentById(id);
        componentRepository.deleteById(id);
    }


    public List<Component> saveAll(Set<Component> components) {
        List<Component> resultList = new ArrayList<>();

        if (components != null && !components.isEmpty()) {
            List<Component> nonDuplicates = components.stream()
                    .filter(component -> !component.fieldsIsNull()).toList();
//                    .filter(component -> notExist(component)).toList();

            for (Component nonDuplicate : nonDuplicates) {
                if (notExist(nonDuplicate)) {
                    resultList.add(componentRepository.save(nonDuplicate));
                } else {
                    resultList.add(search(nonDuplicate));
                }
            }
            // Сохранить все отфильтрованные компоненты
//            return componentRepository.saveAll(nonDuplicates);
//            resultList=resultList.stream().map(ComponentMapper::mapToComponent).toList();
        }
        return resultList;
    }

    public boolean notExist(Component component) {
        try {
            Component existingComponent = search(component);
            if (existingComponent != null) {
                if (existingComponent.getQuantity().equals(component.getQuantity())) {
                    return false;
                }
            }
        } catch (NoSuchElementException e) {
//            System.out.println("no element");
            return true;
        }
        return true;
    }

    public Component search(Component component) throws NoSuchElementException {
        String factoryNumber = component.getFactoryNumber() == "" ? null : component.getFactoryNumber();
        String model = component.getModel() == "" ? null : component.getModel();
        String name = component.getName();
        String unit = component.getUnit();
        String description = component.getDescription() == "" ? null : component.getDescription();

        Component find = componentRepository.findFirstByFactoryNumberAndModelAndNameAndUnitAndDescription(
                factoryNumber, model, name, unit, description);
        return find;
    }
}
