package com.artsemrogovenko.diplom.specification.controller;

import com.artsemrogovenko.diplom.specification.dto.ComponentRequest;
import com.artsemrogovenko.diplom.specification.dto.ComponentResponse;
import com.artsemrogovenko.diplom.specification.model.Component;
import com.artsemrogovenko.diplom.specification.service.ComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/component")
public class ComponentController {

    private final ComponentService componentService;

    @GetMapping
    public ResponseEntity<List<ComponentResponse>> getAll() {
        return new ResponseEntity<>(componentService.getAllComponents(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ComponentResponse> createComponent(@RequestBody ComponentRequest componentRequest) {
        return new ResponseEntity<>(componentService.createComponent(componentRequest), HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<ComponentResponse> getComponent(@PathVariable("id") Long id) {
        ComponentResponse componentResponse;
        try {
            componentResponse = componentService.getComponentById(id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ComponentResponse());
        }
        return new ResponseEntity<>(componentResponse, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<ComponentResponse> updateComponent(@RequestBody ComponentResponse componentRequest) {
        return new ResponseEntity<>(componentService.updateComponent(componentRequest), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteComponent(@PathVariable("id") Long id) {
        componentService.deleteComponent(id);
        return ResponseEntity.ok().build();
    }
}
