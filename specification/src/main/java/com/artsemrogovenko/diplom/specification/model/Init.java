package com.artsemrogovenko.diplom.specification.model;

import com.artsemrogovenko.diplom.specification.repositories.ComponentRepository;
import com.artsemrogovenko.diplom.specification.repositories.ModuleRepository;
import lombok.AllArgsConstructor;
import jakarta.annotation.PostConstruct;

@org.springframework.stereotype.Component
@AllArgsConstructor
public class Init {

    private ComponentRepository componentRepository;
    private ModuleRepository moduleRepository;

    @PostConstruct
    public void init() {
        Component mycompo = new Component();
        mycompo.setName("wire");
        mycompo.setQuantity(2);
        mycompo.setUnit("км");
        mycompo.setDescription("зеленый");
        Module module = new Module();

        module.setName("Коробка");
        module.setModel("С прорезями");
        module.setQuantity(1);
        module.setUnit("шт");
        module.addComponent(mycompo);
        module.setDescription("black color");
        module.setCircuitFile("https://cdn.pixabay.com/photo/2013/07/13/13/48/cardboard-box-161578_1280.png");
        moduleRepository.save(module);
    }
}
