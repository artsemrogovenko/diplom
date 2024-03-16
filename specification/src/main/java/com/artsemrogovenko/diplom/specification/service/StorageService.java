package com.artsemrogovenko.diplom.specification.service;

import com.artsemrogovenko.diplom.specification.api.StorageApi;
import com.artsemrogovenko.diplom.specification.dto.ComponentResponse;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class StorageService {
    private final StorageApi storageApi;
    private final ComponentService componentService;

    private static LocalTime requiredTime = LocalTime.now().plusMinutes(2);
    private static Set<ComponentResponse> components = new HashSet<>();


    /**
     * сколько секунд нужно подождать для повторного обновления списка
     */
    private static int secondsDifference = 0;

    public static int getSecondsDifference() {
        return secondsDifference;
    }

    public Set<ComponentResponse> availableComponents(LocalTime currentTime) {
        ResponseEntity<List<ComponentResponse>> listResponseEntity;

        secondsDifference = (int) LocalTime.now().until(requiredTime, ChronoUnit.SECONDS);

        if (currentTime.isAfter(requiredTime)) {
            requiredTime = LocalTime.now().plusMinutes(2);
            try {
                listResponseEntity = storageApi.getAll();
                if (listResponseEntity.hasBody()) {
                    components.addAll(listResponseEntity.getBody());
                }
            } catch (FeignException ex) {

            }
        }
        components.addAll(getlocalComponents());
        return components;
    }

    private List<ComponentResponse> getlocalComponents() {
        List<ComponentResponse> localList = componentService.getAllComponents();
        return localList;
    }

}

