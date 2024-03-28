package com.artsemrogovenko.diplom.taskmanager.calculate;

import com.artsemrogovenko.diplom.taskmanager.dto.ComponentRequest;
import com.artsemrogovenko.diplom.taskmanager.dto.ComponentResponse;
import com.artsemrogovenko.diplom.taskmanager.dto.mymapper.ComponentMapper;
import com.artsemrogovenko.diplom.taskmanager.dto.mymapper.ModuleMapper;
import com.artsemrogovenko.diplom.taskmanager.model.Component;
import com.artsemrogovenko.diplom.taskmanager.model.Module;
import com.artsemrogovenko.diplom.taskmanager.model.Product;
import com.artsemrogovenko.diplom.taskmanager.model.Task;
import com.artsemrogovenko.diplom.taskmanager.repository.ComponentRepository;
import com.artsemrogovenko.diplom.taskmanager.services.ComponentService;
import com.artsemrogovenko.diplom.taskmanager.services.ModuleService;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@AllArgsConstructor
public class Formula {

    private final ComponentRepository componentRepository;
    private final ModuleService moduleService;
    private final ComponentService componentService;
    private final WebClient.Builder webclientBuilder;

    public void requestComponent() {
        List<ComponentResponse> entity = webclientBuilder.build().get()
                .uri("http://specification-server:8082/inventory")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ComponentResponse>>() {
                })
                .block();
    }

    public List<Task> additionalTask(Product product) {
        Task equipment = new Task();
        equipment.setName("Комплектация");
        equipment.setContractNumber(product.getContractNumber());


        Module special = new Module();
        special.setName("комплектация");
        special.setQuantity(1);
        special.setCircuitFile("https://img.freepik.com/free-photo/rows-crates-large-indoor-warehouse-generated-by-ai_188544-29647.jpg?t=st=1711640057~exp=1711643657~hmac=5819e14cb9400e645cb2ef2580db7876f2c7424e72925ca92151666cf99a6048&w=1380");

        List<Component> additionalComponents = additionalComponents(product);
        special.setComponents(new HashSet<>(additionalComponents));


        Task metal = new Task();
        metal.setName("металл");
        metal.setContractNumber(product.getContractNumber());

        Module controlCabinet = new Module();
        controlCabinet.setName("шкаф");
        controlCabinet.setUnit("шт");
        controlCabinet.setQuantity(1);
        controlCabinet.setCircuitFile("https://www.freepik.com/free-ai-image/robot-working-as-welder-instead-humans_76548463.htm#fromView=search&page=1&position=27&uuid=5d663b47-1032-48c2-8729-daea1190204f");

        controlCabinet.setModel(product.getType());
        controlCabinet.setDescription(product.getColor());

        List<Component> controlCabinetComponents = controlCabinet(product);
        controlCabinet.setComponents(new HashSet<>(controlCabinetComponents));

        Module first = ModuleMapper.mapToModule(moduleService.createModule(controlCabinet).getBody());
        Module second = ModuleMapper.mapToModule(moduleService.createModule(special).getBody());

        metal.addModule(first);
        equipment.addModule(second);
        return new LinkedList<>(List.of(equipment, metal));
    }

    private static List<Component> controlCabinet(Product product) {
        ComponentRequest box = new ComponentRequest().builder()
                .name("корпус").model(product.getType()).quantity(1).unit("шт").description(product.getColor()).build();

        ComponentRequest door = new ComponentRequest().builder()
                .name("дверь").model(product.getType()).quantity(1).unit("шт").description(product.getColor()).build();

        ComponentRequest locks = new ComponentRequest().builder()
                .name("замок").model("20-20/50").quantity(2).unit("шт").description("трехгранный ключ").build();


        List<Component> result = new ArrayList<>();
        result.add(ComponentMapper.mapToComponent(box));
        result.add(ComponentMapper.mapToComponent(door));
        result.add(ComponentMapper.mapToComponent(locks));

        return result;
    }

    public static List<Component> additionalComponents(Product product) {
        Component electricMotor = new Component();
        Component floorDetector = new Component();

        floorDetector.setFactoryNumber("YMP-014");
        floorDetector.setModel("прямоугольный");
        floorDetector.setName("магнит");
        floorDetector.setQuantity(product.getFloors());
        floorDetector.setUnit("шт");
        floorDetector.setDescription("300*20*8mm");

        electricMotor.setUnit("шт");
        electricMotor.setQuantity(1);
        electricMotor.setName("Лебедка");

        if (product.getType().toLowerCase().equals("машинное")) {

            if (product.getLoad() == 1000) {
                electricMotor.setModel("редукторная");
                electricMotor.setFactoryNumber("ЛЛ-296М");
                electricMotor.setDescription("0,5 м/с");
            } else {

                electricMotor.setModel("редукторная");
                electricMotor.setFactoryNumber("ЛЛ-294М");
                electricMotor.setDescription("0,5 м/с");
            }
        }
        if (product.getType().toLowerCase().equals("безмашинное")) {
            if (product.getLoad() == 1000) {

                electricMotor.setModel("безредукторная");
                electricMotor.setFactoryNumber("ЛЛ-1010Б");
                electricMotor.setDescription("1,0");
            } else {
                electricMotor.setModel("безредукторная");
                electricMotor.setFactoryNumber("ЛЛ-0401Б");
                electricMotor.setDescription("1,0");
            }
        }
        return new ArrayList<>(List.of(electricMotor, floorDetector));
    }

    public static List<ComponentRequest> totalizationComponents(List<Component> components) {

        Map<Long, ComponentRequest> uniqueRequest = new HashMap<>();
        for (Component component : components) {
            if (uniqueRequest.containsKey(component.getId())) {
                // Если уже есть такой компонент, обновляем значение quantity
                ComponentRequest tempComponent = uniqueRequest.get(component.getId());
                tempComponent.setQuantity(tempComponent.getQuantity() + component.getQuantity());

            } else {
                // Если такого компонента ещё нет, добавляем его в Map
                ComponentRequest tempComponent = ComponentMapper.mapToComponentRequest(component);
                uniqueRequest.put(component.getId(), tempComponent);
            }
        }
        return new ArrayList<>(uniqueRequest.values());
    }

    public static List<Component> componentsFromAllModules(Task task) {
        List<Component> components = new LinkedList<>();

        for (Module module : task.getModules()) {
            module.getComponents().forEach(components::add);
        }
        return components;
    }
}
