package com.artsemrogovenko.diplom.accountapp.controllers;

import com.artsemrogovenko.diplom.accountapp.api.TaskApi;
import com.artsemrogovenko.diplom.accountapp.aspect.LogMethod;
import com.artsemrogovenko.diplom.accountapp.models.Task;
import com.artsemrogovenko.diplom.accountapp.services.TaskService;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.ConnectException;
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
        return "redirect:/";
    }

    @GetMapping("/myTasks")
    public String myTasks(@RequestBody String userId, Model model) {
        List<Task> as= taskService.showMyTasks(userId);
         model.addAttribute("tasks", as);
        System.out.println(as);
        return "mytasks.html";
    }

    @GetMapping("/")
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
        } catch (WebClientResponseException.ServiceUnavailable | WebClientRequestException | ConnectException ex) {
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

    @LogMethod
    @ExceptionHandler(value = FeignException.InternalServerError.class)
    public String errorPage(FeignException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "redirect:/";
    }

    @LogMethod
    @ExceptionHandler(value = FeignException.MethodNotAllowed.class)
    public String notsupported(FeignException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "redirect:/";
    }

    @LogMethod
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return "index.html"; // Перенаправление на страницу
    }

    @LogMethod
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNoResourceFoundException(NoHandlerFoundException ex) {
        return "index.html"; // Перенаправление на страницу
    }

}
