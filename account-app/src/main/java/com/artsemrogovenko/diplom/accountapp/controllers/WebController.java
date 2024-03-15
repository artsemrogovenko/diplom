package com.artsemrogovenko.diplom.accountapp.controllers;

import com.artsemrogovenko.diplom.accountapp.api.TaskApi;
import com.artsemrogovenko.diplom.accountapp.models.Task;
import com.artsemrogovenko.diplom.accountapp.services.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
public class WebController {
    private final TaskService taskService;
    private final TaskApi taskApi;
    private static List<Task> tasks = new ArrayList<>();
    private static String taskservice_StatusCode;
    private static String responseCode;
    private static String responseMessage;
    private static boolean startView = true;

    @PostMapping("/take/{id}")
    public String takeMeTask(@PathVariable("id") Long taskId, @RequestParam("userid") String userid) {
        ResponseEntity<Void> responseEntity = taskService.takeTask(taskId, userid);
        responseCode = responseEntity.getStatusCode().toString();
        return "/";
    }

    @GetMapping
    public String mainPage(Model model) {
        try {
            if (startView) {
                tasks = taskService.pullTasks();
                startView = false;
            } else {
                List<Task> temp = taskService.getTasks(LocalTime.now());
                if (temp != null && !temp.isEmpty()) {
                    tasks = temp;
                }
            }

        } catch (WebClientResponseException.ServiceUnavailable | WebClientRequestException ex) {
            taskservice_StatusCode = "503 SERVICE_UNAVAILABLE";
        }

        tasks = taskApi.getTasks().getBody();

        model.addAttribute("message", responseCode);
        model.addAttribute("errorInfo", responseMessage);
        model.addAttribute("taskservice", taskservice_StatusCode);
        responseMessage = null;
        taskservice_StatusCode = null;

        model.addAttribute("tasks", tasks);
        model.addAttribute("timer", taskService.getSecondsDifference());
//        System.out.println(modules);
        return "index.html";
    }
}
