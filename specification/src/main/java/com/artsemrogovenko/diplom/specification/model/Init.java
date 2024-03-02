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
        mycompo.setUnit("km");
        mycompo.setDescription("зеленый");

      //  componentRepository.save(mycompo);
        Module module = new Module();

        module.setName("Box");
        module.setQuantity(1);
        module.addComponent(mycompo);
        module.setDescription("black color");
        moduleRepository.save(module);
    }
}
