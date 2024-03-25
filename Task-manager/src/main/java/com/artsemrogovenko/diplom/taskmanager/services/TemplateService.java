package com.artsemrogovenko.diplom.taskmanager.services;

import com.artsemrogovenko.diplom.taskmanager.api.SpecificationApi;
import com.artsemrogovenko.diplom.taskmanager.dto.ModuleResponse;
import com.artsemrogovenko.diplom.taskmanager.dto.mymapper.TemplateMapper;
import com.artsemrogovenko.diplom.taskmanager.model.Template;
import com.artsemrogovenko.diplom.taskmanager.model.TemplateRequest;
import com.artsemrogovenko.diplom.taskmanager.repository.TemplateRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TemplateService {
    private final SpecificationApi specificationApi;
    private final TemplateRepository templateRepository;
    private final ModuleService moduleService;
    private static LocalTime requiredTime = LocalTime.now().plusSeconds(2);
    private static List<ModuleResponse> modules = new ArrayList<>();


    /**
     * сколько секунд нужно подождать для повторного обновления списка
     */
    private static int secondsDifference = 0;

    public static int getSecondsDifference() {
        return secondsDifference;
    }

    /**
     * обновлять список modules не чаще чем раз в две минуты
     *
     * @param currentTime время веб клиента
     */
    public List<ModuleResponse> getSpecifications(LocalTime currentTime) throws feign.RetryableException {
        secondsDifference = (int) LocalTime.now().until(requiredTime, ChronoUnit.SECONDS);
        if (currentTime.isAfter(requiredTime)) {
            requiredTime = LocalTime.now().plusSeconds(2);
            modules = pullModules();
        }
        return modules;
    }


    public List<ModuleResponse> pullModules() throws feign.RetryableException {
        ResponseEntity<List<ModuleResponse>> modulesFromSpecification = specificationApi.getAll();
        if (modulesFromSpecification.hasBody()) {
            modules = modulesFromSpecification.getBody();
        }
        secondsDifference = (int) LocalTime.now().until(requiredTime, ChronoUnit.SECONDS);
        return modules;
    }

    public ResponseEntity<String> prepareData(TemplateRequest rawTemplate, String selectedModulesJson, List<ModuleResponse> list) {
        List<String> selectedModuleIds = null;
        TemplateRequest temp = rawTemplate;
        if (isExist(rawTemplate.getName(), rawTemplate.getDescription())) {
            return new ResponseEntity<>("Шаблон с таким именем уже есть", HttpStatus.CONFLICT);
        }
        // Преобразование JSON в список идентификаторов выбранных модулей
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            selectedModuleIds = objectMapper.readValue(selectedModulesJson, new TypeReference<List<String>>() {
            });
            // Создание списка модулей на основе идентификаторов
            if (selectedModuleIds != null && !selectedModuleIds.isEmpty()) {
                for (String position : selectedModuleIds) {

                    try {
                        temp.addModule(list.get(Integer.parseInt(position)));
                    } catch (NullPointerException | IndexOutOfBoundsException ex) {
                        return new ResponseEntity<>("Список доступных модулей устарел, повторите попытку", HttpStatus.BAD_REQUEST);
                    }

                }
            }
            saveTemplate(TemplateMapper.mapToTemplate(temp));
            return new ResponseEntity<>("Шаблон сохранен", HttpStatus.CREATED);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
        return new ResponseEntity<>("Ошибка выполнения", HttpStatus.BAD_REQUEST);
    }

    @Transactional
    void saveTemplate(Template template) {
        template.getModules().forEach(module -> moduleService.createModule(module));
        templateRepository.save(template);
    }

    private boolean isExist(String name, String description) {
        Optional<Template> ex = templateRepository.findByNameAndDescription(name, description);
        if (ex.isPresent()) {
            return true;
        }
        return false;
    }

    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }
}
