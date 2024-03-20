package com.artsemrogovenko.diplom.accountapp.services;


import com.artsemrogovenko.diplom.accountapp.aspect.LogMethod;
import com.artsemrogovenko.diplom.accountapp.dto.mymapper.ModuleMapper;
import com.artsemrogovenko.diplom.accountapp.models.Component;
import com.artsemrogovenko.diplom.accountapp.repositories.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.artsemrogovenko.diplom.accountapp.models.Module;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

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
//        return moduleRepository.findById(id)
//                .orElseThrow(() -> new NoSuchElementException("Module not found with id: " + id));
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
            if (module.getComponents() != null) {

                List<Component> componentList = componentService.saveAll(module.getComponents());
                if (componentList != null) {
                    module.setComponents(new HashSet<>());
                    componentList.forEach(component -> module.addComponent(component));
                }
            }
            Module result = moduleRepository.save(module);

            System.out.println(moduleRepository.findLastModule());
            return new ResponseEntity<>(result, CREATED);
        }
        return new ResponseEntity<>(searchModule(module), HttpStatus.CONFLICT);
    }


    public boolean notExist(Module module) {
        try {
            Module existingModule = searchModule(module);
            if(existingModule.getQuantity()!=null){
                if (existingModule.getQuantity().equals(module.getQuantity())) {
                    return false;
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

    //    @LogMethod
//    private Module searchModule(Module module) throws NoSuchElementException {
//        String factoryNumber = module.getFactoryNumber();
//        String model = module.getModel();
//        String name = module.getName();
//        String unit = module.getUnit();
//        String description = module.getDescription();
//        String circutFile = module.getCircutFile();
//
//        return moduleRepository.findByFactoryNumberAndModelAndNameAndUnitAndDescriptionAndCircutFile(
//                factoryNumber, model, name, unit, description, circutFile).get();
//    }
    @LogMethod
    private Module searchModule(Module module)  throws NoSuchElementException{
        String factoryNumber = module.getFactoryNumber() == "" ? null : module.getFactoryNumber();
        String model = module.getModel() == "" ? null : module.getModel();
        String name = module.getName();
        String unit = module.getUnit();
        String description = module.getDescription() == "" ? null : module.getDescription();
        String circutFile = module.getCircutFile() == "" ? null : module.getCircutFile();

        return moduleRepository.findDistinctFirstByFactoryNumberAndModelAndNameAndUnitAndDescriptionAndCircutFile(factoryNumber, model, name, unit, description, circutFile).get();
    }
}
