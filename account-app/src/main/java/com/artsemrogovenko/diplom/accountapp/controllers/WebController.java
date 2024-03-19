package com.artsemrogovenko.diplom.accountapp.controllers;

import com.artsemrogovenko.diplom.accountapp.api.TaskApi;
import com.artsemrogovenko.diplom.accountapp.aspect.LogMethod;
import com.artsemrogovenko.diplom.accountapp.models.Account;
import com.artsemrogovenko.diplom.accountapp.models.Task;
import com.artsemrogovenko.diplom.accountapp.services.AccountService;
import com.artsemrogovenko.diplom.accountapp.services.TaskService;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("")
@Secured({ "ROLE_USER", "ROLE_ADMIN" })
//@PostAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
public class WebController {
    private final TaskService taskService;
    private final AccountService accountService;
    private final TaskApi taskApi;
    private static List<Task> tasks = new ArrayList<>();
    private static String taskservice_StatusCode;
    private static String responseCode;
    private static String responseMessage;
    private static boolean startView = true;
    private static String confirm;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("account", new Account());
        return "registration.html";
    }

    @PostMapping("/registration")
    public String addUser(Account userForm, Model model) {
        if (!accountService.saveUser(userForm)) {
            System.out.println("сработала ошибка");
            model.addAttribute("message", "Пользователь с таким именем уже существует");
            return "redirect:/registration";
        }
        return "redirect:/";
    }


    @PostMapping("/take/{id}")
    public String takeMeTask(@PathVariable("id") Long taskId, @RequestParam("userid") String userid) {
        ResponseEntity<String> responseEntity = ResponseEntity.ok().body(null);
        try {
            responseEntity = taskService.takeTask(taskId, userid);
            confirm = responseEntity.getBody();
            System.out.println(confirm);
        } catch (FeignException.InternalServerError ex) {
            responseMessage = responseEntity.getBody();
            System.out.println(responseMessage);
            System.out.println(ex.contentUTF8());

        } catch (FeignException.NotFound ex) {
            responseMessage = ex.contentUTF8();
        }
//        if (responseEntity.getStatusCode().is2xxSuccessful()) {
//            confirm = responseEntity.getBody();
//        } else {
//            responseMessage = responseEntity.getStatusCode().toString() + responseEntity.getBody();
//        }
        return "redirect:/";
    }

    @PostMapping("/")
    public String login() {
        return "redirect:/";
    }

    @GetMapping("/myTasks")
    public String myTasks(@RequestParam("userId") String userId, Model model) {
        List<Task> my = taskService.showMyTasks(userId);
        model.addAttribute("tasks", my);
        System.out.println(my);
        return "mytasks.html";
    }

    @LogMethod
    @GetMapping("/")
    public String mainPage(Model model) {
        // Получаем объект аутентификации из SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Получаем имя пользователя из объекта аутентификации
        String username = authentication.getName();
        // Добавляем имя пользователя в модель для передачи его в представление
        model.addAttribute("username", username);
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
        } catch (WebClientResponseException.ServiceUnavailable | WebClientRequestException |
                 feign.RetryableException ex) {
            taskservice_StatusCode = "503 SERVICE_UNAVAILABLE";
        }
//        tasks = taskApi.getTasks().getBody();

        model.addAttribute("message", responseCode);
        model.addAttribute("confirm", confirm);
        model.addAttribute("errorInfo", responseMessage);
        model.addAttribute("taskservice", taskservice_StatusCode);
        responseMessage = null;
        taskservice_StatusCode = null;
        confirm = null;
        model.addAttribute("tasks", tasks);
        model.addAttribute("timer", taskService.getSecondsDifference());
//        System.out.println(modules);
        return "index.html";
    }

    @GetMapping("/errorPage")
    public String errorPage(ModelAndView model) {
        model.addObject("taskservice", taskservice_StatusCode);
        responseMessage = null;
        taskservice_StatusCode = null;
        return "errorPage";
    }


}
