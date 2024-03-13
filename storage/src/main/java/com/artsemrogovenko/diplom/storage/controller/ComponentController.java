package com.artsemrogovenko.diplom.storage.controller;

import com.artsemrogovenko.diplom.storage.dto.ComponentRequest;
import com.artsemrogovenko.diplom.storage.dto.ComponentResponse;
import com.artsemrogovenko.diplom.storage.model.Component;
import com.artsemrogovenko.diplom.storage.service.ComponentService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/megaImport")
    public ResponseEntity<?> saveComponents(@RequestBody List<ComponentRequest> components) {
        return new ResponseEntity<>(componentService.increaseComponents(components), HttpStatus.ACCEPTED);
    }

    @GetMapping("{name}")
    public ResponseEntity<List<ComponentResponse>> getComponent(@PathVariable("name") String name) {
        List<ComponentResponse> componentResponses;
        try {
            componentResponses = componentService.getComponentByName(name);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return new ResponseEntity<>(componentResponses, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<ComponentResponse> editComponent(@RequestBody ComponentResponse componentResponse) {
        return new ResponseEntity<>(componentService.editComponent(componentResponse), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteComponent(@PathVariable("id") Long id) {
        componentService.deleteComponent(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/inventory")
    public ResponseEntity<List<ComponentResponse>> isInStock(@RequestBody  List<ComponentRequest> requests) {
        return componentService.isInStock(requests);
    }

    @PostMapping("/reserve")
    public ResponseEntity<List<ComponentResponse>> reserve(@RequestBody List<ComponentRequest> requests, @RequestParam String user, @RequestParam String number) {
        return componentService.reserveComponents(number,user,requests);
    }
}
