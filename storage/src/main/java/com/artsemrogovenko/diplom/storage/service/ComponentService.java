package com.artsemrogovenko.diplom.storage.service;

import com.artsemrogovenko.diplom.storage.aspect.LogMethod;
import com.artsemrogovenko.diplom.storage.dto.ComponentData;
import com.artsemrogovenko.diplom.storage.dto.ComponentRequest;
import com.artsemrogovenko.diplom.storage.dto.ComponentResponse;
import com.artsemrogovenko.diplom.storage.dto.mymapper.ComponentMapper;
import com.artsemrogovenko.diplom.storage.model.Component;
import com.artsemrogovenko.diplom.storage.repositories.ComponentRepository;
import com.artsemrogovenko.diplom.storage.repositories.AccountRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Metrics;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComponentService {
    private final DeficitService deficitService;
    private final ComponentRepository componentRepository;
    private final AccountRepository accountRepository;

    private final Counter subtask = Metrics.counter("Подзадач запрошено");
    private final Counter noResource = Metrics.counter("Отказ и-за нехватки");

    private final Gauge percentGauge = Gauge.builder("Процент успеха склада", () -> 1.0 / percent)
            .register(Metrics.globalRegistry);


    private static double percent = 1;

    public ComponentResponse createComponent(ComponentRequest newComponent) {
        Component dist = findDistinct(newComponent);
        HashMap<Integer, String> units = convertUnits(newComponent);
        try {
            if (dist != null) {
                // например гайки
                if (dist.getRefill().booleanValue() == true) {
                    dist.setQuantity(dist.getQuantity() + units.keySet().iterator().next());
                    return ComponentMapper.mapToComponentResponse(componentRepository.save(dist));
                    //например бухта кабеля
                } else {
                    newComponent.setRefill(dist.getRefill());
                    return saveComponent(newComponent);
                }
            }
        } catch (NoSuchElementException n) { // если такого элемента нет
        }
        return saveComponent(newComponent);
    }

    public String increaseComponents(List<ComponentRequest> newComponents) {
        for (ComponentRequest newComponent : newComponents) {
            createComponent(newComponent);
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

    public ComponentResponse saveComponent(ComponentRequest componentRequest) {
        ComponentRequest temp = componentRequest;
        HashMap<Integer, String> units = convertUnits(componentRequest);
        if (temp.getRefill() == null) {
            temp.setRefill(false);
        }
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

    /**
     * @param request найти объект с такими параметрами
     * @return список полных совпадений
     * @throws NoSuchElementException
     */
    private List<Component> findComponents(ComponentRequest request) throws NoSuchElementException {
        String factoryNumber = request.getFactoryNumber() == "" ? null : request.getFactoryNumber();
        String model = request.getModel() == "" ? null : request.getModel();
        String name = request.getName();
        String unit = request.getUnit();
        String description = request.getDescription() == "" ? null : request.getDescription();

        List<Component> result = componentRepository.findAllByFactoryNumberAndModelAndNameAndUnitAndDescription(factoryNumber, model, name, unit, description).get();
        if (result.isEmpty() || result == null) {
            throw new NoSuchElementException();
        }
        return result;
    }

    /**
     * Есть ли компоненты на складе
     *
     * @param requests список желаемого
     * @return
     */
    public ResponseEntity<List<ComponentResponse>> isInStock(List<ComponentRequest> requests) {
        List<ComponentResponse> components = new ArrayList<>();

        for (ComponentRequest request : requests) {


            List<Component> list = new ArrayList<>();
            int requiredQuantity = request.getQuantity();

            try {
                if (request.getUnit().toLowerCase().equals("м")) {
                    requiredQuantity = (request.getQuantity() * 1000);
                    request.setUnit("мм");
                }
                if (request.getUnit().toLowerCase().equals("км")) {
                    requiredQuantity = (request.getQuantity() * 1000000);
                    request.setUnit("мм");
                }
                //  Пробую получить свойство boolean. Если нет такого элемента в базе данных, перейду к списку закупок
                if (request.getRefill() == null) {
                    try {
                        Component temp = findDistinct(request);
                        request.setRefill(temp.getRefill());
                    } catch (NullPointerException ex) {
                        throw new NoSuchElementException();
                    }
                }

                list = findComponents(request);
                components.add(checkList(list, requiredQuantity));
            } catch (NoSuchElementException e) { // если такого элемента нет в базе данных
                ComponentResponse newResponse = ComponentResponse.builder()
                        .factoryNumber(request.getFactoryNumber())
                        .model(request.getModel())
                        .name(request.getName())
                        .quantity(requiredQuantity * (-1))
                        .unit(request.getUnit())
                        .description(request.getDescription())
                        .refill(true).build(); // это список для закупок, поэтому лучше объединить повторы
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
        // Если нет отриц. полей.
        return new ResponseEntity<>(components, HttpStatus.OK);
    }

    /**
     * Ищу подходящий элемент начиная с наименьшего значения.
     * Например, если это 2 куска провода, выбор будет сделан на наименьший отрезок
     *
     * @param list             список по характеристикам, может содержать разное количество
     * @param requiredQuantity желаемое количество компонента
     * @return вернется элемент такой как нужно, либо сгенерируется значение недостачи
     */

    private ComponentResponse checkList(List<Component> list, int requiredQuantity) {
        ComponentResponse reqComponent;
        //проверяю весть список, у всех ли компонентов количество меньше требуемого
        boolean NonSufficient = list.stream().allMatch(component -> component.getQuantity() < requiredQuantity);

        //если нет нужного количества отправлю объект с отрицательным значением
        if (NonSufficient) {
            ComponentResponse deficient = ComponentMapper.mapToComponentResponse(list.stream().max(Comparator.comparingInt(Component::getQuantity)).get());

            if (deficient.getRefill() == null) {
                deficient.setRefill(findDistinct(deficient).getRefill());
            }
            if (deficient.getQuantity() < requiredQuantity) {
                // Уведомить какое значение quantity необходимо
                if (deficient.getRefill() == true) {
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

    /**
     * Если исполнитель запрашивает задачу на выполнение
     *
     * @param contractNumber     на какой договор
     * @param taskId             номер задачи
     * @param userId             кто взял
     * @param requiredComponents список компонентов
     * @return ответ о наличии всех компонентов
     */
    @Transactional
    @LogMethod
    public ResponseEntity<List<ComponentResponse>> reserveComponents(String contractNumber, Long taskId, String userId, List<ComponentRequest> requiredComponents) {
        ResponseEntity<List<ComponentResponse>> response = isInStock(requiredComponents);
        subtask.increment();
        if (response.getStatusCode().isSameCodeAs(HttpStatus.I_AM_A_TEAPOT)) {
            System.out.println("вызван метод закупки");
            noResource.increment();
            deficitService.addToCard(contractNumber, taskId, userId, response.getBody());
        }
        if (response.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
            System.out.println("компоненты есть");
        }
        return response;
    }

    private <T extends ComponentData> Component findDistinct(T component) {
        String factoryNumber = component.getFactoryNumber() == "" ? null : component.getFactoryNumber();
        String model = component.getModel() == "" ? null : component.getModel();
        String name = component.getName();
        String unit = component.getUnit();
        String description = component.getDescription() == "" ? null : component.getDescription();

        Component exiting = null;
        try {
            exiting = componentRepository.findFirstByFactoryNumberAndModelAndNameAndUnitAndDescription(factoryNumber, model, name, unit, description);
        } catch (NoSuchElementException ex) {
        }
        return exiting;

    }

}
