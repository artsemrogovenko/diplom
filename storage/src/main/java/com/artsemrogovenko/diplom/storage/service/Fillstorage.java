package com.artsemrogovenko.diplom.storage.service;

import com.artsemrogovenko.diplom.storage.dto.ComponentRequest;
import com.artsemrogovenko.diplom.storage.model.Component;
import com.artsemrogovenko.diplom.storage.repositories.ComponentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Component
public class Fillstorage {
    @Autowired
    ComponentService componentService;
    @PostConstruct
    public void init() {
        ComponentRequest component1 = ComponentRequest.builder().factoryNumber("79436").name("НШВИ 0.75-8").quantity(4).unit("шт").refill(true).build();
        ComponentRequest component2 = ComponentRequest.builder().factoryNumber("00-00012187").model("-").description("-").name("МКЭШнг(А)-LS 7х0.75 500В").quantity(5).unit("м").refill(false).build();

        componentService.createComponent(component1);
        componentService.createComponent(component2);
    }
}
