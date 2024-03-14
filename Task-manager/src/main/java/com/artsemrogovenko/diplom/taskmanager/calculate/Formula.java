package com.artsemrogovenko.diplom.taskmanager.calculate;

import com.artsemrogovenko.diplom.taskmanager.dto.ComponentRequest;
import com.artsemrogovenko.diplom.taskmanager.dto.ComponentResponse;
import com.artsemrogovenko.diplom.taskmanager.dto.ModuleRequest;
import com.artsemrogovenko.diplom.taskmanager.dto.ModuleResponse;
import com.artsemrogovenko.diplom.taskmanager.dto.mymapper.ComponentMapper;
import com.artsemrogovenko.diplom.taskmanager.model.*;
import com.artsemrogovenko.diplom.taskmanager.model.Module;
import com.artsemrogovenko.diplom.taskmanager.repository.ComponentRepository;
import com.artsemrogovenko.diplom.taskmanager.services.ComponentService;
import com.artsemrogovenko.diplom.taskmanager.services.ModuleService;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class Formula {

    private final ComponentRepository componentRepository;
    private final ModuleService moduleService;
    private final WebClient.Builder webclientBuilder;

    public void requestComponent() {
        List<ComponentResponse> entity = webclientBuilder.build().get()
                .uri("http://specification-server:8082/inventory")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ComponentResponse>>() {
                })
                .block();
    }

    public Task additionalTask(Product product) {
        Task equipment = new Task();
        equipment.setName("комплектация");
        equipment.setContractNumber(product.getContractNumber());

        Module special = new Module();
        special.setName("для договора " + product.getContractNumber());
        special.setQuantity(1);

        MyCollection<Component> components = new MyCollection<>(Component.class, additionalComponents(product));

        special.setComponents(components);

        Module controlCabinet = new Module();
        controlCabinet.setName("шкаф управления");
        controlCabinet.setQuantity(1);
        controlCabinet.setModel(product.getType());
        controlCabinet.setDescription(product.getColor());

        moduleService.createModule(controlCabinet);
        moduleService.createModule(special);


        equipment.addModule(controlCabinet);
        equipment.addModule(special);

        return equipment;
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
}
