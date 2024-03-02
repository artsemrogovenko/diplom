package com.artsemrogovenko.diplom.specification.service;

import com.artsemrogovenko.diplom.specification.dto.ModuleRequest;
import com.artsemrogovenko.diplom.specification.dto.ModuleResponse;
import com.artsemrogovenko.diplom.specification.dto.mymapper.ComponentMapper;
import com.artsemrogovenko.diplom.specification.dto.mymapper.ModuleMapper;
import com.artsemrogovenko.diplom.specification.model.Module;
import com.artsemrogovenko.diplom.specification.repositories.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleService {
    private final ComponentService componentService;
    private final ModuleRepository moduleRepository;

    public List<ModuleResponse> getAllModules() {
        return moduleRepository.findAll().stream()
                .map(module ->
                        ModuleResponse.builder()
                                .id(module.getId())
                                .factoryNumber(module.getFactoryNumber())
                                .model(module.getModel())
                                .name(module.getName())
                                .quantity(module.getQuantity())
                                .unit(module.getUnit())
                                .description(module.getDescription())
                                .componentResponses(module.getComponents().stream()
                                        .map(component -> ComponentMapper.mapToComponentResponse(component))
                                        .collect(Collectors.toSet()))
                                .build()
                ).toList();
    }

    public ModuleResponse getModuleById(Long id) {
        return ModuleMapper.mapModuleToModuleResponse(moduleRepository.findById(id).orElseThrow(null)) ;
    }

    public ModuleResponse updateModule(ModuleResponse module) {
        Module moduleById = ModuleMapper.mapToModule(getModuleById(module.getId()));

        moduleById.setId(module.getId());
        moduleById.setFactoryNumber(module.getFactoryNumber());
        moduleById.setModel(module.getModel());
        moduleById.setName(module.getName());
        moduleById.setQuantity(module.getQuantity());
        moduleById.setUnit(module.getUnit());
        moduleById.setDescription(module.getDescription());

        moduleById.setComponents(module.getComponentResponses().stream()
                        .map(componentResponse -> ComponentMapper.mapToComponent(componentResponse))
                        .collect(Collectors.toSet())    );

        return  ModuleMapper.mapModuleToModuleResponse(moduleRepository.save(moduleById)) ;
    }

    public ModuleResponse createModule(ModuleRequest moduleRequest) {
        Module module = ModuleMapper.mapToModule(moduleRequest);
        componentService.saveAll(module.getComponents());
        return ModuleMapper.mapModuleToModuleResponse( moduleRepository.save(module)) ;
    }

    public void deleteModule(Long id) {
        moduleRepository.deleteById(id);
    }


}
