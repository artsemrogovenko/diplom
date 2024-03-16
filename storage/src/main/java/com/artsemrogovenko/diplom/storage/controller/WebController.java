package com.artsemrogovenko.diplom.storage.controller;

import com.artsemrogovenko.diplom.storage.dto.ComponentRequest;
import com.artsemrogovenko.diplom.storage.dto.ComponentResponse;
import com.artsemrogovenko.diplom.storage.service.DeficitService;
import com.artsemrogovenko.diplom.storage.service.ComponentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
public class WebController {

    private final ComponentService componentService;
    private final DeficitService deficitService;

    @GetMapping("")
    public String getAll(Model model) {
        model.addAttribute("componentRequest",new ComponentRequest());
        model.addAttribute("componentResponse",new ComponentResponse());
        List<ComponentResponse> list= componentService.getAllComponents();
        model.addAttribute("components",list);
        return "index";
    }

    @PostMapping("/createComponent")
    public String createComponent(ComponentRequest componentRequest) {
//        System.out.println(componentRequest.toString());
        componentService.createComponent(componentRequest);
//        redirectAttributes.addAttribute("confirm", "компонент добавлен");
        return "redirect:/";
    }

    @GetMapping("/buy")
    public String showCard(Model model) {
        model.addAttribute("needs", deficitService.getAll());
        return "card";
    }

    @GetMapping("/{name}")
    public String getComponent(@PathVariable("name") String name,Model model, RedirectAttributes redirectAttributes) {
        List<ComponentResponse> componentResponses;
        try {
            componentResponses = componentService.getComponentByName(name);
            model.addAttribute("components", componentResponses);
        } catch (RuntimeException e) {
            redirectAttributes.addAttribute("message", "совпадений нет");
        }
        return "redirect:/";
    }

    @PostMapping("/update")
    public String editComponent(ComponentResponse componentResponse) {
        componentService.editComponent(componentResponse);
        return "redirect:/";
    }


    @PostMapping("/delete/{id}")
    public String deleteComponent(@PathVariable("id") Long id) {
        componentService.deleteComponent(id);
        return "redirect:/";
    }

    @ExceptionHandler(value = RuntimeException.class)
    public String errorPage(Principal principal, RuntimeException e, Model model,
                            HttpServletRequest request) {
        model.addAttribute("message", e.getMessage());
//        model.addAttribute("tasks", webService.getAll());
        return "index";
    }
}
