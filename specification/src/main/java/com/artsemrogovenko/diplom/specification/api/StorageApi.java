package com.artsemrogovenko.diplom.specification.api;

import com.artsemrogovenko.diplom.specification.dto.ComponentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "storage-server")
public interface StorageApi {
    @GetMapping("/component")
    ResponseEntity<List<ComponentResponse>> getAll();
}
