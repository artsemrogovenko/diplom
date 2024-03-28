package com.artsemrogovenko.diplom.taskmanager.api;

import com.artsemrogovenko.diplom.taskmanager.dto.ModuleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient("specification-server")
public interface SpecificationApi {

    @GetMapping("/module")
    public ResponseEntity<List<ModuleResponse>> getAll();

    @GetMapping("/")
    public String main(Model model);
}
