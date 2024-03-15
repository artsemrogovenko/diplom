package com.artsemrogovenko.diplom.accountapp.httprequest;


import com.artsemrogovenko.diplom.accountapp.models.Component;
import com.artsemrogovenko.diplom.accountapp.models.ComponentMapper;
import com.artsemrogovenko.diplom.accountapp.models.ComponentRequest;
import com.artsemrogovenko.diplom.accountapp.models.Task;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyRequest {
    public static ResponseEntity<?> rollbackComponents(List<ComponentRequest> components) {
        // Создаем экземпляр RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Устанавливаем заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Создаем тело запроса
        HttpEntity<List<ComponentRequest>> requestEntity = new HttpEntity<>(components, headers);

        // Указываем URL эндпоинта, куда отправляется запрос
        String url = "http://storage-server:8081/megaImport";

        // Отправляем POST-запрос
        ResponseEntity<?> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                Object.class); // Ожидаемый тип ответа

        return responseEntity;
    }

    public static List<ComponentRequest> totalizationComponents(List<Component> components) {

        Map<Long, ComponentRequest> uniqueRequest = new HashMap<>();
        for (Component component : components) {
            if (uniqueRequest.containsKey(component.getId())) {
                // Если уже есть такой компонент, обновляем значение quantity
                ComponentRequest tempComponent = uniqueRequest.get(component.getId());
                tempComponent.setQuantity(tempComponent.getQuantity() + component.getQuantity());

            } else {
                // Если такого компонента ещё нет, добавляем его в Map
                ComponentRequest tempComponent = ComponentMapper.mapToComponentRequest(component);
                uniqueRequest.put(component.getId(), tempComponent);
            }
        }
        return new ArrayList<>(uniqueRequest.values());
    }

    public static List<Component> componentsFromAllModules(Task task) {
        List<Component> components = task.getModules().stream()
                .flatMap(module -> module.getComponents().stream())
                .toList();
        return components;
    }

}
