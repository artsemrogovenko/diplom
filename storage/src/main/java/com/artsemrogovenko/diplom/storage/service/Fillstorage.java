package com.artsemrogovenko.diplom.storage.service;

import com.artsemrogovenko.diplom.storage.model.Component;
import com.artsemrogovenko.diplom.storage.repositories.ComponentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Component
public class Fillstorage {
//    @Autowired
    ComponentRepository componentRepository;
//    @PostConstruct
    public void init() {
        Component mycompo = new Component();
        mycompo.setName("wire");
        mycompo.setQuantity(2);
        mycompo.setUnit("km");
        mycompo.setDescription("зеленый");
        componentRepository.save(mycompo);
    }
}
