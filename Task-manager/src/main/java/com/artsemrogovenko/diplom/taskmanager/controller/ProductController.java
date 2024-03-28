package com.artsemrogovenko.diplom.taskmanager.controller;

import com.artsemrogovenko.diplom.taskmanager.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@AllArgsConstructor
public class ProductController {
    private final ProductRepository productRepository;
    @PostMapping("/delete")
    public void delTask(@RequestBody String id) {
        productRepository.deleteById(id);
    }
}
