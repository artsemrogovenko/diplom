package com.artsemrogovenko.diplom.taskmanager.services;

import com.artsemrogovenko.diplom.taskmanager.calculate.Formula;
import com.artsemrogovenko.diplom.taskmanager.dto.ModuleResponse;
import com.artsemrogovenko.diplom.taskmanager.dto.mymapper.ModuleMapper;
import com.artsemrogovenko.diplom.taskmanager.dto.mymapper.TemplateMapper;
import com.artsemrogovenko.diplom.taskmanager.model.Module;
import com.artsemrogovenko.diplom.taskmanager.model.*;
import com.artsemrogovenko.diplom.taskmanager.repository.ModuleRepository;
import com.artsemrogovenko.diplom.taskmanager.repository.ProductRepository;
import com.artsemrogovenko.diplom.taskmanager.repository.TaskRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ModuleService moduleService;
    private final TaskRepository taskRepository;
    private final ModuleRepository moduleRepository;
    private final Formula formulaService;

    private final Counter subtask = Metrics.counter("Subtasks created");

    public ResponseEntity<String> prepareData(Product product, String selectedTemplatesJson, String selectedModulesJson, List<Template> templateList, List<ModuleResponse> moduleResponseList) {
        // Преобразование JSON в список идентификаторов выбранных модулей
        List<String> selectedTemplateIds = null;
        List<String> selectedModulesIds = null;
        moduleResponseList.forEach(moduleResponse -> moduleResponse.setId(null));
        if (isExist(product)) {
            return new ResponseEntity<>("Такой экземпляр уже есть", HttpStatus.CONFLICT);
        }
        Product newProduct = product;
        newProduct.setDone(false);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            selectedTemplateIds = objectMapper.readValue(selectedTemplatesJson, new TypeReference<List<String>>() {
            });
            selectedModulesIds = objectMapper.readValue(selectedModulesJson, new TypeReference<List<String>>() {
            });

            // Создание списка модулей на основе идентификаторов
            List<Template> selectedTemplates = new ArrayList<>();
            List<ModuleResponse> selectedModules = new ArrayList<>();

            if (selectedModulesIds != null && !selectedModulesIds.isEmpty()) {
                for (String position : selectedModulesIds) {
                    try {
                        selectedModules.add(moduleResponseList.get(Integer.parseInt(position)));
                    } catch (NullPointerException | IndexOutOfBoundsException ex) {
                        return new ResponseEntity<>("Список модулей устарел, повторите попытку", HttpStatus.BAD_REQUEST);
                    }
                }
            }

            if (selectedTemplateIds != null && !selectedTemplateIds.isEmpty()) {
                for (String position : selectedTemplateIds) {
                    try {
                        selectedTemplates.add(templateList.get(Integer.parseInt(position)));
                    } catch (NullPointerException | IndexOutOfBoundsException ex) {
                        return new ResponseEntity<>("Список шаблонов устарел, повторите попытку", HttpStatus.BAD_REQUEST);
                    }
                }

                List<Task> taskList = new ArrayList<>();
                for (Template selectedTemplate : selectedTemplates) {
                    Task temp = TemplateMapper.mapToTask(selectedTemplate);
                    temp.getModules().forEach(module -> module.setFactoryNumber(product.getContractNumber()));
                    temp.setContractNumber(product.getContractNumber());
                    taskList.add(temp);
                }

                taskList.forEach(task -> task.setContractNumber(product.getContractNumber()));
//                taskRepository.saveAll(taskList);
                newProduct.getTasks().addAll(taskList);
            }

            //добавляю дополнительные компоненты
            List<Task> additional = formulaService.additionalTask(newProduct);
            for (Task task : additional) {
                task.getModules();
                taskRepository.save(task);
            }
            newProduct.getTasks().addAll(additional);
/**
 * Добавлены модули из формы задачи
 */
            if (!selectedModules.isEmpty()) {
                List<Module> moduleList = new ArrayList<>();
                for (ModuleResponse selectedModule : selectedModules) {
                    ModuleResponse response = moduleService.createModule(ModuleMapper.mapToModule(selectedModule)).getBody();
                    Module module = ModuleMapper.mapToModule(response);
                    module.setFactoryNumber(product.getContractNumber());

                    moduleList.add(module);
                }
                moduleList.forEach(moduleResponse -> {
                    if (moduleResponse != null) {
                        Task newtask = new Task();
                        newtask.setContractNumber(product.getContractNumber());
                        newtask.setName(moduleResponse.getName());
                        newtask.setDescription(moduleResponse.getDescription());
                        newtask.addModule(moduleRepository.findById(moduleResponse.getId()).get());
                        newProduct.getTasks().add(taskRepository.save(newtask));
                    }
                });
            }
            saveProduct(newProduct);

//            System.out.println(productRepository.findById(newProduct.getContractNumber()));

            return new ResponseEntity<>("Продукт принят в производство", HttpStatus.CREATED);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
        return new ResponseEntity<>("Ошибка выполнения", HttpStatus.BAD_REQUEST);
    }


    void saveProduct(Product product) {
        productRepository.save(product);
        product.getTasks().forEach(task -> subtask.increment(task.getModules().size()));
    }

    private boolean isExist(Product product) {
        Optional<Product> exiting = productRepository.findByContractNumberAndTypeAndLoadAndColorAndFloors(
                product.getContractNumber(), product.getType(), product.getLoad(), product.getColor(), product.getFloors());
        if (exiting.isPresent()) {
            return true;
        }
        return false;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Если все подзадачи сделаны, Product меняет статус
     * @param contractnumber
     */
    public void changeProductStatus(String contractnumber) {
        Product verifyProduct = productRepository.findById(contractnumber).get();
        if (verifyProduct.getTasks().stream().allMatch(task -> task.getStatus().equals(TaskStatus.DONE))) {
            verifyProduct.setDone(true);
        }

    }


}
