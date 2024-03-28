package com.artsemrogovenko.diplom.taskmanager.services;

import com.artsemrogovenko.diplom.taskmanager.dto.ComponentResponse;
import com.artsemrogovenko.diplom.taskmanager.dto.ModuleRequest;
import com.artsemrogovenko.diplom.taskmanager.dto.ModuleResponse;
import com.artsemrogovenko.diplom.taskmanager.dto.SavedModule;
import com.artsemrogovenko.diplom.taskmanager.dto.mymapper.ComponentMapper;
import com.artsemrogovenko.diplom.taskmanager.dto.mymapper.ModuleMapper;
import com.artsemrogovenko.diplom.taskmanager.model.Component;
import com.artsemrogovenko.diplom.taskmanager.model.Module;
import com.artsemrogovenko.diplom.taskmanager.model.MyCollection;
import com.artsemrogovenko.diplom.taskmanager.repository.ModuleRepository;
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
    private final ComponentService componentService;
    private final ModuleRepository moduleRepository;

    /**
     * Чтобы для преобразования одной модели в другую не занимать столько места,
     * я сделал Классы с методами преобразования пакете /dto/mymapper
     * @return
     */
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
                                .componentResponses(new MyCollection<>(ComponentResponse.class, module.getComponents().stream()
                                        .map(component -> ComponentMapper.mapToComponentResponse(component))
                                        .collect(Collectors.toSet())))
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

    /**
     * Сохранение и привязка модуля к сущности
     * @param moduleRequest
     * @return
     * @param <T> любой класс имплементирующий SavedModule
     */
    public <T extends SavedModule> ResponseEntity<ModuleResponse> createModule(T moduleRequest) {
        if (moduleRequest == null) {
            return new ResponseEntity<>(new ModuleResponse(), HttpStatus.BAD_REQUEST);
        }
        Module module = new Module();

        if (moduleRequest instanceof Module) {
            module = (Module) moduleRequest;
        }
        if (moduleRequest instanceof ModuleResponse) {
            module = ModuleMapper.mapToModule((ModuleRequest) moduleRequest);
        }
        module.setId(null);
        if (notExist(module)) {
            if (module.getComponents() != null) {
                List<Component> componentList = componentService.saveAll(module.getComponents());
                if (componentList != null) {
                    module.setComponents(new HashSet<>(componentList));
                }
            }
            ModuleResponse result = ModuleMapper.mapModuleToModuleResponse(moduleRepository.save(module));

//            System.out.println(moduleRepository.findLastModule());
            return new ResponseEntity<>(result, CREATED);
        }
        Module newmodule = ModuleMapper.mapToModule(module);
        List<Component> componentList = componentService.saveAll(newmodule.getComponents());
        newmodule.setComponents(new HashSet<>(componentList));

        return new ResponseEntity<>(ModuleMapper.mapModuleToModuleResponse(moduleRepository.save(newmodule)), HttpStatus.CONFLICT);
    }

    public boolean notExist(Module module) {
        try {
            Module existingModule = searchModule(module);
            if (existingModule != null) {
                if (existingModule.getQuantity() != null) {
                    if (existingModule.getQuantity().equals(module.getQuantity()) && existingModule.getComponents() == module.getComponents()) {
                        return false;
                    }
                }
            }
        } catch (NoSuchElementException e) {
            return true;
        }
        return true;
    }

    public void deleteModule(Long id) {
        moduleRepository.deleteById(id);
    }

    /**
     * Поиск на совпадение по полям
     * @param module
     * @return модуль или null
     * @throws NoSuchElementException
     */
    public Module searchModule(Module module) throws NoSuchElementException {
        String factoryNumber = module.getFactoryNumber() == "" ? null : module.getFactoryNumber();
        String model = module.getModel() == "" ? null : module.getModel();
        String name = module.getName();
        Integer quantity = module.getQuantity();
        String unit = module.getUnit();
        String description = module.getDescription() == "" ? null : module.getDescription();
        String circutFile = module.getCircuitFile() == "" ? null : module.getCircuitFile();

        Module result = moduleRepository.findFirstByFactoryNumberAndModelAndNameAndQuantityAndUnitAndDescriptionAndCircuitFile(
                factoryNumber, model, name, quantity, unit, description, circutFile);

        return result;
    }

}
