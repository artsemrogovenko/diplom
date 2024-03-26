package com.artsemrogovenko.diplom.specification.controller;

import com.artsemrogovenko.diplom.specification.dto.ModuleRequest;
import com.artsemrogovenko.diplom.specification.dto.ModuleResponse;
import com.artsemrogovenko.diplom.specification.service.ModuleService;
import com.artsemrogovenko.diplom.specification.service.StorageService;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@Controller
@AllArgsConstructor
public class WebController {
    private final ModuleService moduleService;
    private final StorageService storageService;
    private static boolean startView = true;
    private static String response;
    private static String taskResponse;
    private static List<ModuleResponse> sessionList;

    @GetMapping("/task")
    public String taskSever() throws FeignException {
        return "redirect:http://localhost:8085";
    }
    @GetMapping("/")
    public String main(Model model) throws Exception {

        if (startView) {
            pullList();
            startView = false;
        }

        model.addAttribute("components", storageService.availableComponents(LocalTime.now()));
        model.addAttribute("newModule", new ModuleRequest());
        model.addAttribute("errorInfo", taskResponse);
        taskResponse = null;
        model.addAttribute("modules", sessionList);
        model.addAttribute("message", response);
        response = null;
        return "index";
    }

    @PostMapping("/createModule")
    public String create(ModuleRequest moduleRequest, Model model) {
        ResponseEntity<ModuleResponse> responseEntity = moduleService.createModule(moduleRequest);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            pullList();
        }
        response = responseEntity.getStatusCode().toString();
        return "redirect:/";
    }

    @PostMapping("/updateModule")
    public String update(ModuleResponse moduleResponse, Model model) {
        System.out.println(moduleResponse.toString());
        pullList();
        return "redirect:/";
    }

    @PostMapping("/deleteModule/{id}")
    public String delete(@PathVariable("id") Long moduleId) {
        moduleService.deleteModule(moduleId);
        pullList();
        return "redirect:/";
    }


    private void pullList() {
        sessionList = moduleService.getAllModules();
    }


}
