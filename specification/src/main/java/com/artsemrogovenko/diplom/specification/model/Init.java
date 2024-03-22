package com.artsemrogovenko.diplom.specification.model;

import com.artsemrogovenko.diplom.specification.dto.ComponentRequest;
import com.artsemrogovenko.diplom.specification.dto.ModuleRequest;
import com.artsemrogovenko.diplom.specification.dto.mymapper.ModuleMapper;
import com.artsemrogovenko.diplom.specification.repositories.ComponentRepository;
import com.artsemrogovenko.diplom.specification.repositories.ModuleRepository;
import com.artsemrogovenko.diplom.specification.service.ModuleService;
import lombok.AllArgsConstructor;
import jakarta.annotation.PostConstruct;

import java.util.Collection;
import java.util.List;

@org.springframework.stereotype.Component
@AllArgsConstructor
public class Init {

    private ComponentRepository componentRepository;
    private ModuleRepository moduleRepository;
    private ModuleService moduleService;

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
//        moduleRepository.save(module);
        if(moduleService.notExist(module)){
            moduleRepository.save(module);
        }
    }
}

