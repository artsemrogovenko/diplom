package com.artsemrogovenko.diplom.specification.controller;

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
    public ResponseEntity<List<Component>> getAll() {
        return new ResponseEntity<>(componentService.getAllComponents(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Component> createComponent(@RequestBody Component Component) {
        return new ResponseEntity<>(componentService.createComponent(Component), HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<Component> getComponent(@PathVariable("id") Long id) {
        Component Component;
        try {
            Component = componentService.getComponentById(id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Component());
        }
        return new ResponseEntity<>(Component, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Component> updateComponent(@RequestBody Component Component) {
        return new ResponseEntity<>(componentService.updateComponent(Component), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteComponent(@PathVariable("id") Long id) {
        componentService.deleteComponent(id);
        return ResponseEntity.ok().build();
    }
}
