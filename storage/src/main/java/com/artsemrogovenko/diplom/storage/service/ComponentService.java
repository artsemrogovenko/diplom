package com.artsemrogovenko.diplom.storage.service;

import com.artsemrogovenko.diplom.storage.dto.ComponentData;
import com.artsemrogovenko.diplom.storage.dto.ComponentRequest;
import com.artsemrogovenko.diplom.storage.dto.ComponentResponse;
import com.artsemrogovenko.diplom.storage.dto.mymapper.ComponentMapper;
import com.artsemrogovenko.diplom.storage.model.Component;
import com.artsemrogovenko.diplom.storage.repositories.ComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComponentService {

    private final ComponentRepository componentRepository;

    public ComponentResponse increaseComponent(ComponentRequest newComponent) {
        Component componentByFields;
        try {
            componentByFields = findComponents(newComponent).stream().max(Comparator.comparingInt(Component::getQuantity)).get();
            if (componentByFields.isRefill() && componentByFields != null) {

                HashMap<Integer, String> units = convertUnits(newComponent);
                componentByFields.setQuantity(componentByFields.getQuantity() + units.keySet().iterator().next());

            } else {
                componentByFields = ComponentMapper.mapToComponent(createComponent(newComponent));
            }

        } catch (NoSuchElementException n) { // если такого элемента нет
            componentByFields = ComponentMapper.mapToComponent(createComponent(newComponent));
        }
        return ComponentMapper.mapToComponentResponse(componentRepository.save(componentByFields));
    }

    public String increaseComponents(List<ComponentRequest> newComponents) {
        for (ComponentRequest newComponent : newComponents) {
            increaseComponent(newComponent);
        }
        return new String("Склад пополнился всеми компонентами");
    }

    public ComponentResponse decreaseComponent(ComponentResponse decreasing) {
        Component componentById = componentRepository.findById(decreasing.getId()).orElseThrow();
//        if (componentById.isRefill()) {
        componentById.setQuantity(componentById.getQuantity() - decreasing.getQuantity());
//        }
        componentRepository.save(componentById);
        return decreasing;
    }


    public void deleteComponent(Long id) {
        componentRepository.deleteById(id);
    }

    public List<ComponentResponse> getComponentByName(String name) {
        return componentRepository.findAllByName(name).stream().map(component -> ComponentMapper.mapToComponentResponse(component)).toList();

    }

    public List<ComponentResponse> getAllComponents() {
        return componentRepository.findAll().stream().map(component -> ComponentMapper.mapToComponentResponse(component)).toList();
    }

    private static <T extends ComponentData> HashMap<Integer, String> convertUnits(T component) {
//        if(component.getUnit().toLowerCase()!="шт"){
        if (component.getUnit().toLowerCase().equals("м")) {
            return new HashMap<Integer, String>(Map.of(component.getQuantity() * 1000, "мм"));
        }
        if (component.getUnit().toLowerCase().equals("км")) {
            return new HashMap<Integer, String>(Map.of(component.getQuantity() * 1000000, "мм"));
        }
//            return new HashMap<Integer, String>(Map.of(component.getQuantity(), component.getUnit()));
//        }
        return new HashMap<Integer, String>(Map.of(component.getQuantity(), component.getUnit().toLowerCase()));
    }

    public ComponentResponse createComponent(ComponentRequest componentRequest) {
        ComponentRequest temp = componentRequest;
        HashMap<Integer, String> units = convertUnits(componentRequest);

        temp.setQuantity(units.keySet().iterator().next());
        temp.setUnit(units.values().iterator().next());

//        System.out.println(" item = " + temp.getQuantity() + temp.getUnit());
        return ComponentMapper.mapToComponentResponse(componentRepository.save(ComponentMapper.mapToComponent(temp)));
    }

    public ComponentResponse editComponent(ComponentResponse componentResponse) {

        Component componentById = componentRepository.findById(componentResponse.getId()).orElse(null);
        if (componentById != null) {

            componentById = ComponentMapper.mapToComponent(componentResponse);

            HashMap<Integer, String> units = convertUnits(componentResponse);

            componentById.setQuantity(units.keySet().iterator().next());
            componentById.setUnit(units.values().iterator().next());
            return ComponentMapper.mapToComponentResponse(componentRepository.save(componentById));
        }
        return null;
    }

    private List<Component> findComponents(ComponentRequest request) throws NoSuchElementException {
        String factoryNumber = request.getFactoryNumber();
        String model = request.getModel();
        String name = request.getName();
        String unit = request.getUnit();
        String description = request.getDescription();

        return componentRepository.findByFactoryNumberAndModelAndNameAndUnitAndDescription(factoryNumber, model, name, unit, description)
                .orElseThrow(NoSuchElementException::new);
    }

    //    @Transactional(readOnly = true)
    public ResponseEntity<List<ComponentResponse>> isInStock(List<ComponentRequest> requests) {
//        System.out.println("isInStock");
        List<ComponentResponse> components = new ArrayList<>();
//        List<ComponentResponse> responses = new ArrayList<>();
        for (ComponentRequest request : requests) {

            int requiredQuantity = request.getQuantity();

            List<Component> list = new ArrayList<>();
            try {
                list = findComponents(request);
                components.add(checkList(list, requiredQuantity));
            } catch (NoSuchElementException e) { // если такого элемента совсем не существует
                ComponentResponse newResponse = ComponentResponse.builder()
                        .factoryNumber(request.getFactoryNumber())
                        .model(request.getModel())
                        .name(request.getName())
                        .quantity(requiredQuantity * -1)
                        .unit(request.getUnit())
                        .description(request.getDescription()).build();
                components.add(newResponse);
            }

//            components.addAll(list);
        }
        List<ComponentResponse> negativeQuantityComponents = components.stream()
                .filter(component -> component.getQuantity() < 0).toList();

        if (!negativeQuantityComponents.isEmpty()) {
            // Обработка случая, когда есть элементы с отрицательным значением quantity
            return new ResponseEntity<>(negativeQuantityComponents, HttpStatus.I_AM_A_TEAPOT);
        } else {
            components.stream().forEach(componentResponse -> decreaseComponent(componentResponse));
            //updateComponents(responses);
        }
        // если нет отриц. полей
        return new ResponseEntity<>(components, HttpStatus.OK);
    }

    private ComponentResponse checkList(List<Component> list, int requiredQuantity) {
        ComponentResponse reqComponent;
        boolean NonSufficient = list.stream().allMatch(component -> component.getQuantity() < requiredQuantity);

        //если нет нужного количества отплавлю обьект с отрицательным значением
        if (NonSufficient) {
            ComponentResponse deficient = ComponentMapper.mapToComponentResponse(list.stream().max(Comparator.comparingInt(Component::getQuantity)).get());
            if (deficient.getQuantity() < requiredQuantity) {
                // Уведомить какое значение quantity необходимо
                if (deficient.isRefill()) {
                    deficient.setQuantity((requiredQuantity - deficient.getQuantity()) * -1); // купить недостачу
                } else {
                    deficient.setQuantity(requiredQuantity * -1); // купить сколько запрашивается
                }
            }
            return deficient;
        } else {//если количество в одном элементе равно либо больше запрашиваемого
            list = list.stream().sorted(Comparator.comparingInt(Component::getQuantity)).collect(Collectors.toList());
            for (Component component : list) {// начинаю с самого наименьшего значения
                if (component.getQuantity() >= requiredQuantity) {

                    reqComponent = ComponentMapper.mapToComponentResponse(component);
                    reqComponent.setQuantity(requiredQuantity);
                    return reqComponent; // добавлю подходящий элемент с нужным количеством
                }
            }
        }
        return null;
    }

    @Transactional
    public ResponseEntity<List<ComponentResponse>> reserveComponents(String contractNumber,String userId, List<ComponentRequest> requiredComponents) {
        ResponseEntity<List<ComponentResponse>> response = isInStock(requiredComponents);
        if (response.getStatusCode().isSameCodeAs(HttpStatus.I_AM_A_TEAPOT)) {
            //TODO сделать уведомление для заказа
        }
        if (response.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
            //TODO сделать перевод компонентов клиенту
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
