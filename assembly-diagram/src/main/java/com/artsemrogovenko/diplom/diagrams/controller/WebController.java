package com.artsemrogovenko.diplom.diagrams.controller;

import com.artsemrogovenko.diplom.diagrams.model.ContractNumber;
import com.artsemrogovenko.diplom.diagrams.model.DiagramDescription;
import com.artsemrogovenko.diplom.diagrams.service.DiagramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebController {
    private final DiagramService diagramService;
    private String response;

    @GetMapping("/")
    public String main(Model model) {
        System.out.println(model);
        model.addAttribute("description", new DiagramDescription());
        model.addAttribute("contractNumber", new ContractNumber());
        model.addAttribute("elements", diagramService.getAllmodulesNames());
        model.addAttribute("message", response);
        System.out.println(response);
        response = null;
        return "index3";
    }

    @GetMapping("/elements")
    public String showElements(Model model) {
        model.addAttribute("elements", diagramService.getAllmodulesNames());
        return "elements";
    }

    @PostMapping("/upload/mix")
    public String uploadmix(@RequestParam("fileContent") MultipartFile file, DiagramDescription description, String contractNumber, Model model) {
        HttpStatus statusCode = HttpStatus.valueOf(diagramService.saveToRepositiry(file, description, contractNumber).getStatusCode().value());

        response = statusCode.toString();

        return "redirect:/"; // Редирект на главную страницу
    }

}
