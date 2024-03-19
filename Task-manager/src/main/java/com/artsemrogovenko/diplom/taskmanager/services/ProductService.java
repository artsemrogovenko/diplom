package com.artsemrogovenko.diplom.taskmanager.services;

import com.artsemrogovenko.diplom.taskmanager.calculate.Formula;
import com.artsemrogovenko.diplom.taskmanager.dto.mymapper.TemplateMapper;
import com.artsemrogovenko.diplom.taskmanager.model.Product;
import com.artsemrogovenko.diplom.taskmanager.model.Task;
import com.artsemrogovenko.diplom.taskmanager.model.Template;
import com.artsemrogovenko.diplom.taskmanager.repository.ProductRepository;
import com.artsemrogovenko.diplom.taskmanager.repository.TaskRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final TaskRepository taskRepository;

    private final Formula formulaService;


    public ResponseEntity<String> prepareData(Product product, String selectedTemplatesJson, List<Template> templateList) {
        // Преобразование JSON в список идентификаторов выбранных модулей
        List<String> selectedTemplateIds = null;
        if (isExist(product)) {
            return new ResponseEntity<>("Такой экземпляр уже есть", HttpStatus.CONFLICT);
        }
        Product newProduct = product;
        newProduct.setDone(false);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            selectedTemplateIds = objectMapper.readValue(selectedTemplatesJson, new TypeReference<List<String>>() {
            });
            // Создание списка модулей на основе идентификаторов
            List<Template> selectedTemplates = new ArrayList<>();

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
//                    temp.getModules().addAll(selectedTemplate.getModules());
                    taskList.add(temp);
                }

                taskList.forEach(task -> task.setContractNumber(product.getContractNumber()));
//                taskRepository.saveAll(taskList);
                newProduct.getTasks().addAll(taskList);
            }
            //добавляю дополнительные компоненты
            List<Task> additional = formulaService.additionalTask(newProduct);
            for (Task task : additional) {
                taskRepository.save(task);
            }
            newProduct.getTasks().addAll(additional);


            saveProduct(newProduct);
            System.out.println(productRepository.findById(newProduct.getContractNumber()));

            return new ResponseEntity<>("Продукт принят в производство", HttpStatus.CREATED);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
        return new ResponseEntity<>("Ошибка выполнения", HttpStatus.BAD_REQUEST);
    }


    void saveProduct(Product product) {
//        if (product.getTasks() != null && !product.getTasks().isEmpty()) {
//            taskRepository.saveAll(product.getTasks());
//        }
        productRepository.save(product);
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
}
