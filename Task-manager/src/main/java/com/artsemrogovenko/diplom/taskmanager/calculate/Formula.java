package com.artsemrogovenko.diplom.taskmanager.calculate;

import com.artsemrogovenko.diplom.taskmanager.dto.ComponentRequest;
import com.artsemrogovenko.diplom.taskmanager.dto.ComponentResponse;
import com.artsemrogovenko.diplom.taskmanager.dto.ModuleResponse;
import com.artsemrogovenko.diplom.taskmanager.model.Component;
import com.artsemrogovenko.diplom.taskmanager.model.Product;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import com.artsemrogovenko.diplom.taskmanager.config.WebClientConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class Formula {
    private final WebClient.Builder webclientBuilder;

    public void requestComponent() {
        List<ComponentResponse> entity = webclientBuilder.build().get()
                .uri("http://specification-server:8082/inventory")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ComponentResponse>>() {
                })
                .block();
    }

    public List<ComponentRequest> calculateComponents(Product product) {
        ComponentRequest electricMotor = new ComponentRequest();
        ComponentRequest floorDetector = new ComponentRequest();

        floorDetector.setFactoryNumber("YMP-014");
        floorDetector.setName("магнит");
        floorDetector.setModel("прямоугольный");
        floorDetector.setDescription("300*20*8mm");
        floorDetector.setUnit("шт");
        floorDetector.setQuantity(product.getFloors());

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
