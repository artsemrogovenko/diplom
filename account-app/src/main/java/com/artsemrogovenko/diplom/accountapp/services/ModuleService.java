package com.artsemrogovenko.diplom.accountapp.services;


import com.artsemrogovenko.diplom.accountapp.aspect.LogMethod;
import com.artsemrogovenko.diplom.accountapp.dto.mymapper.ModuleMapper;
import com.artsemrogovenko.diplom.accountapp.models.Component;
import com.artsemrogovenko.diplom.accountapp.models.Module;
import com.artsemrogovenko.diplom.accountapp.repositories.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;

@Service
@RequiredArgsConstructor
public class ModuleService {
    private final ComponentService componentService;
    private final ModuleRepository moduleRepository;

    public List<Module> getAllModules() {
        return moduleRepository.findAll();
    }

    @LogMethod
    public Module getModuleById(Long id) {
        Optional<Module> moduleOptional = moduleRepository.findById(id);
        if (moduleOptional.isPresent()) {
            Module module = moduleOptional.get();
            // Загрузка компонентов
            module.getComponents().size(); // Принудительная загрузка компонентов
            return module;
        }
        return null;
    }

    public Module updateModule(Module module) {
        Module moduleById = getModuleById(module.getId());

        return moduleRepository.save(moduleById);
    }

    @LogMethod
    public ResponseEntity<Module> createModule(Module moduleRequest) {
        if (moduleRequest == null) {
            return new ResponseEntity<>(new Module(), HttpStatus.BAD_REQUEST);
        }

        Module module = ModuleMapper.mapToModule(moduleRequest);
        if (notExist(module)) {
            if (module.getComponents() != null && !module.getComponents().isEmpty()) {

                List<Component> componentList = componentService.saveAll(module.getComponents());
                if (componentList != null) {
                    module.setComponents(new HashSet<>(componentList));
                }
            }
            Module result = moduleRepository.save(module);

            System.out.println(moduleRepository.findLastModule());
            return new ResponseEntity<>(result, CREATED);
        }
        Module savedModule = searchModule(module);
        return new ResponseEntity<>(savedModule, HttpStatus.CONFLICT);
    }


    public boolean notExist(Module module) {
        try {
            Module existingModule = searchModule(module);
            if (existingModule.getQuantity() != null) {
                if (existingModule.getQuantity().equals(module.getQuantity()) && existingModule.getComponents().equals(module.getComponents())) {
                    return true;
                }
            }
        } catch (NoSuchElementException e) {
            return true;
        }
        return false;
    }

    public void deleteModule(Long id) {
        moduleRepository.deleteById(id);
    }

    @LogMethod
    private Module searchModule(Module module) throws NoSuchElementException {
        String factoryNumber = module.getFactoryNumber() == "" ? null : module.getFactoryNumber();
        String model = module.getModel() == "" ? null : module.getModel();
        String name = module.getName();
        Integer quantity = module.getQuantity();
        String unit = module.getUnit();
        String description = module.getDescription() == "" ? null : module.getDescription();
        String circutFile = module.getCircuitFile() == "" ? null : module.getCircuitFile();

        Optional<Module> find = moduleRepository.findFirstByFactoryNumberAndModelAndNameAndQuantityAndUnitAndDescriptionAndCircuitFile(factoryNumber, model, name, quantity, unit, description, circutFile);

        if (find.isPresent()) {
            return find.get();
//            module.setComponents(componentService.getComponentById(find.get().getId()));
        }else {
            throw new NoSuchElementException();
        }

    }

    public List<Module> getModuleByTask(Long taskId) {
        return moduleRepository.findAllByTasks_Id(taskId);
    }

    public Module findModuleById(Long id) {
        try {
            return moduleRepository.findById(id).get();
        } catch (NoSuchElementException e) {
        }
        return null;
    }
}
