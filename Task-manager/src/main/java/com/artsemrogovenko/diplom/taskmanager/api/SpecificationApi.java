package com.artsemrogovenko.diplom.taskmanager.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@FeignClient("specification-server")
public interface SpecificationApi {
    @GetMapping("/")
    String specificationMainPage(Model model);
}
