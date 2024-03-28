package com.artsemrogovenko.diplom.specification.service;

import com.artsemrogovenko.diplom.specification.api.AssemblyApi;
import com.artsemrogovenko.diplom.specification.dto.ModuleRequest;
import com.artsemrogovenko.diplom.specification.dto.ModuleResponse;
import com.artsemrogovenko.diplom.specification.dto.mymapper.ComponentMapper;
import com.artsemrogovenko.diplom.specification.dto.mymapper.ModuleMapper;
import com.artsemrogovenko.diplom.specification.model.Component;
import com.artsemrogovenko.diplom.specification.model.DiagramDescription;
import com.artsemrogovenko.diplom.specification.model.Module;
import com.artsemrogovenko.diplom.specification.repositories.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CREATED;

@Service
@RequiredArgsConstructor
public class ModuleService {
    private final AssemblyApi assemblyApi;
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
                                .circuitFile(module.getCircuitFile())
                                .build()
                ).toList();
    }

    public ModuleResponse getModuleById(Long id) {
        return ModuleMapper.mapModuleToModuleResponse(
                moduleRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Module not found with id: " + id)));
    }

    public ModuleResponse updateModule(ModuleResponse module) {
        Module moduleById = ModuleMapper.mapToModule(getModuleById(module.getId()));

        return ModuleMapper.mapModuleToModuleResponse(moduleRepository.save(moduleById));
    }

    public ResponseEntity<ModuleResponse> createModule(ModuleRequest moduleRequest) {
        if (moduleRequest == null) {
            return new ResponseEntity<>(new ModuleResponse(), HttpStatus.BAD_REQUEST);
        }
        Module module = ModuleMapper.mapToModule(moduleRequest);
        DiagramDescription diagramrequest = new DiagramDescription(module.getName(), module.getModel(), module.getCircuitFile());
        String circuit = assemblyApi.requestSheme(diagramrequest).getBody();
        module.setCircuitFile(circuit);

        if (notExist(module)) {
            if (module.getComponents() != null) {
                List<Component> componentList = componentService.saveAll(module.getComponents());
                if (componentList != null) {
                    componentList.addAll(componentList);
                    module.setComponents(new HashSet<>(componentList));
                }
            }
            ModuleResponse result = ModuleMapper.mapModuleToModuleResponse(moduleRepository.save(module));
            return new ResponseEntity<>(result, CREATED);
        }
        return new ResponseEntity<>(new ModuleResponse(), HttpStatus.CONFLICT);
    }


    public boolean notExist(Module module) {
        String factoryNumber = module.getFactoryNumber() == "" ? null : module.getFactoryNumber();
        String model = module.getModel() == "" ? null : module.getModel();
        String name = module.getName();
        String unit = module.getUnit();
        String description = module.getDescription() == "" ? null : module.getDescription();
        try {
            Module existingModule = moduleRepository.findFirstByFactoryNumberAndModelAndNameAndUnitAndDescription(factoryNumber, model, name, unit, description).get();
        } catch (NoSuchElementException e) {
            return true;
        }
        return false;
    }

    public void deleteModule(Long id) {
        System.out.println(moduleRepository.findById(id));
        moduleRepository.deleteById(id);
    }


}
