package com.artsemrogovenko.diplom.taskmanager.services;

import com.artsemrogovenko.diplom.taskmanager.dto.ModuleResponse;
import com.artsemrogovenko.diplom.taskmanager.dto.mymapper.TemplateMapper;
import com.artsemrogovenko.diplom.taskmanager.model.Template;
import com.artsemrogovenko.diplom.taskmanager.model.TemplateRequest;
import com.artsemrogovenko.diplom.taskmanager.repository.TemplateRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TemplateService {

    private final WebClient.Builder webclientBuilder;
    private final TemplateRepository templateRepository;
    private final ModuleService moduleService;
    private static LocalTime requiredTime = LocalTime.now().plusMinutes(2);
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
    public List<ModuleResponse> getSpecifications(LocalTime currentTime) throws WebClientResponseException.ServiceUnavailable, WebClientRequestException {
//        System.out.println("rquired time = " + String.format("%02d:%02d:%02d", requiredTime.getHour(), requiredTime.getMinute(), requiredTime.getSecond()));
//        System.out.println("current time = " + String.format("%02d:%02d:%02d", currentTime.getHour(), currentTime.getMinute(), currentTime.getSecond()));
        secondsDifference = (int) LocalTime.now().until(requiredTime, ChronoUnit.SECONDS);
        if (currentTime.isAfter(requiredTime)) {
            requiredTime = LocalTime.now().plusMinutes(2);
//            secondsDifference = (int) LocalTime.now().until(requiredTime, ChronoUnit.SECONDS);
            modules = pullModules();
        }
        return modules;
    }


    public List<ModuleResponse> pullModules() throws WebClientResponseException.ServiceUnavailable, WebClientRequestException {
        modules = webclientBuilder.build().get()
                .uri("http://specification-server:8082/module")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ModuleResponse>>() {
                })
                .block();
        secondsDifference = (int) LocalTime.now().until(requiredTime, ChronoUnit.SECONDS);
        return modules;
    }

    public ResponseEntity<String> prepareData(TemplateRequest rawTemplate, String selectedModulesJson, List<ModuleResponse> list) {
        // Преобразование JSON в список идентификаторов выбранных модулей
        List<String> selectedModuleIds = null;
        TemplateRequest temp = rawTemplate;
        ResponseEntity<String> response;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            selectedModuleIds = objectMapper.readValue(selectedModulesJson, new TypeReference<List<String>>() {
            });
            // Создание списка модулей на основе идентификаторов
            List<ModuleResponse> selectedModules = new ArrayList<>();
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
//        System.out.println(temp);
        return new ResponseEntity<>("Ошибка выполнения", HttpStatus.BAD_REQUEST);
    }

    @Transactional
    void saveTemplate(Template template) {
        template.getModules().forEach(module -> moduleService.createModule(module));
        templateRepository.save(template);
    }

    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }
}
