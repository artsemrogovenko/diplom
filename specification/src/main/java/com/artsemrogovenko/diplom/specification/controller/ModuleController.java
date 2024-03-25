package com.artsemrogovenko.diplom.specification.controller;

import com.artsemrogovenko.diplom.specification.aspect.LogMethod;
import com.artsemrogovenko.diplom.specification.dto.ModuleRequest;
import com.artsemrogovenko.diplom.specification.dto.ModuleResponse;
import com.artsemrogovenko.diplom.specification.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/module")
public class ModuleController {

    private final ModuleService moduleService;

    @LogMethod
    @GetMapping
    public ResponseEntity<List<ModuleResponse>> getAll() {
        return new ResponseEntity<>(moduleService.getAllModules(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ModuleResponse> createComponent(@RequestBody ModuleRequest module) {
        return moduleService.createModule(module);
    }

    @GetMapping("{id}")
    public ResponseEntity<ModuleResponse> getComponent(@PathVariable("id") Long id) {
        ModuleResponse module;
        try {
            module = moduleService.getModuleById(id);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ModuleResponse());
        }
        return new ResponseEntity<>(module, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<ModuleResponse> updateComponent(@RequestBody ModuleResponse module) {
        return new ResponseEntity<>(moduleService.updateModule(module), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteComponent(@PathVariable("id") Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.ok().build();
    }
}