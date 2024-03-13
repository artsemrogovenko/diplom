package com.artsemrogovenko.diplom.taskmanager.controller;

import com.artsemrogovenko.diplom.taskmanager.config.WebClientConfig;
import com.artsemrogovenko.diplom.taskmanager.dto.ModuleResponse;
import com.artsemrogovenko.diplom.taskmanager.model.Product;
import com.artsemrogovenko.diplom.taskmanager.model.Template;
import com.artsemrogovenko.diplom.taskmanager.model.TemplateRequest;
import com.artsemrogovenko.diplom.taskmanager.services.ProductService;
import com.artsemrogovenko.diplom.taskmanager.services.TemplateService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.channels.ClosedChannelException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
public class WebController {
    private static boolean startView = true;
    private final ProductService productService;
    private final TemplateService templateService;
    private static List<ModuleResponse> modules = new ArrayList<>();
    private static List<Template> templates ;
    private static String specification_StatusCode = "";
    private static String responseCode = "";


    //    private static LocalTime time = LocalTime.now();
//    private static int totalSeconds = 0;
    @GetMapping("/")
    public String main(Model model) {
        model.addAttribute("products",productService.getAllProducts());
        return "index";
    }

    @PostMapping("/createTemplate")
    public String createTemplate(@ModelAttribute("template") TemplateRequest rawTemplate, @RequestParam("selectedModules") String selectedModulesJson) {
        ResponseEntity<String> result = templateService.prepareData(rawTemplate, selectedModulesJson, modules);
        responseCode = result.getStatusCode().toString();
        return "redirect:/constructTempate";
    }

    @PostMapping("/createProduct")
    public String createProduct(@ModelAttribute("product") Product product, @RequestParam("selectedTemplates") String selectedTemplatesJson) {
        System.out.println(templates);
        ResponseEntity<String> result = productService.prepareData(product, selectedTemplatesJson, templates);
        responseCode = result.getStatusCode().toString();
        return "redirect:/constructProduct";
    }

    @GetMapping("/constructProduct")
    public String constuctProduct(Model model, String message) {
        templates = templateService.getAllTemplates();
        System.out.println(templates);
        model.addAttribute("message", responseCode);
        model.addAttribute("product", new Product());
//        model.addAttribute("dependencies", new ModuleResponse());
        model.addAttribute("templates", templates);
//        model.addAttribute("timer", templateService.getSecondsDifference());
        return "productConstructor.html";
    }

    @GetMapping("/constructTempate")
    public String constuctTemplate(Model model, String message) {
        try {
            if (startView) {
                modules = templateService.pullModules();
                startView = false;
            } else {
                List<ModuleResponse> temp = templateService.getSpecifications(LocalTime.now());
                if (temp != null && !temp.isEmpty()) {
                    modules = temp;
                }
            }

        } catch (WebClientResponseException.ServiceUnavailable | WebClientRequestException ex) {
            specification_StatusCode = "503 SERVICE_UNAVAILABLE";
        }
        model.addAttribute("specificationservice", specification_StatusCode);
        model.addAttribute("message", responseCode);
        specification_StatusCode = "";
        model.addAttribute("template", new TemplateRequest());
//        model.addAttribute("dependencies", new ModuleResponse());
        model.addAttribute("specifications", modules);
        model.addAttribute("timer", templateService.getSecondsDifference());
//        System.out.println(modules);
        return "templateConstructor.html";
    }


}
