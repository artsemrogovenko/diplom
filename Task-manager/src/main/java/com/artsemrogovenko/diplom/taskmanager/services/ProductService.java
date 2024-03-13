package com.artsemrogovenko.diplom.taskmanager.services;

import com.artsemrogovenko.diplom.taskmanager.dto.mymapper.TemplateMapper;
import com.artsemrogovenko.diplom.taskmanager.model.Module;
import com.artsemrogovenko.diplom.taskmanager.model.Product;
import com.artsemrogovenko.diplom.taskmanager.model.Task;
import com.artsemrogovenko.diplom.taskmanager.model.Template;
import com.artsemrogovenko.diplom.taskmanager.model.TemplateData;
import com.artsemrogovenko.diplom.taskmanager.repository.ModuleRepository;
import com.artsemrogovenko.diplom.taskmanager.repository.ProductRepository;
import com.artsemrogovenko.diplom.taskmanager.repository.TaskRepository;
import com.artsemrogovenko.diplom.taskmanager.repository.TemplateRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final TemplateRepository templateRepository;
    private final TaskRepository taskRepository;

    private final ModuleRepository moduleRepository;

    public ResponseEntity<String> prepareData(Product product, String selectedTemplatesJson, List<Template> templateList) {
        System.out.println(templateList);
        // Преобразование JSON в список идентификаторов выбранных модулей
        List<String> selectedTemplateIds = null;
        Product newProduct = product;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            selectedTemplateIds = objectMapper.readValue(selectedTemplatesJson, new TypeReference<List<String>>() {
            });
            // Создание списка модулей на основе идентификаторов
            List<Template> selectedTemplates = new ArrayList<>();

            if (selectedTemplateIds != null && !selectedTemplateIds.isEmpty()) {
                for (String position : selectedTemplateIds) {
                    selectedTemplates.add(templateList.get(Integer.parseInt(position)));
                }
//                List<Module> templateModules = templateList.stream()
//                        .flatMap(template -> moduleRepository.findByTemplateId(template.getId()).stream())
//                        .toList();

//                System.out.println(templateModules);
//                List<Task> taskList = selectedTemplates.stream().map(template -> templateRepository.findById(template.getId())
//                                .map(TemplateMapper::mapToTask).orElse(null)) // или другое значение по умолчанию
//                                .filter(Objects::nonNull) // фильтруем null значения, если такие есть
//                                .toList();
                List<Task> taskList = new ArrayList<>();
                for (Template selectedTemplate : selectedTemplates) {
                    Task temp = TemplateMapper.mapToTask(selectedTemplate);
                    temp.setModules(selectedTemplate.getModules());
                    taskList.add(temp);
                }

                taskList.forEach(task -> task.setContractNumber(product.getContractNumber()));
                newProduct.setTasks(taskList);
            }

            saveProduct(newProduct);
            return new ResponseEntity<>("Продукт создан", HttpStatus.CREATED);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
//        System.out.println(newProduct);
        return new ResponseEntity<>("Ошибка выполнения", HttpStatus.BAD_REQUEST);
    }


    void saveProduct(Product product) {
        if (product.getTasks() != null && !product.getTasks().isEmpty()) {
            taskRepository.saveAll(product.getTasks());
        }
        productRepository.save(product);
    }


    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
