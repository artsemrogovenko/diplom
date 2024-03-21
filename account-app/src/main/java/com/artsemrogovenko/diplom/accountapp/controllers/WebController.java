package com.artsemrogovenko.diplom.accountapp.controllers;

import com.artsemrogovenko.diplom.accountapp.aspect.LogMethod;
import com.artsemrogovenko.diplom.accountapp.models.Account;
import com.artsemrogovenko.diplom.accountapp.models.Module;
import com.artsemrogovenko.diplom.accountapp.models.Task;
import com.artsemrogovenko.diplom.accountapp.services.AccountService;
import com.artsemrogovenko.diplom.accountapp.services.TaskService;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("")
public class WebController {
    private final TaskService taskService;
    private final AccountService accountService;
    private static String taskservice_StatusCode;
    private static String responseCode;
    private static String responseMessage;
    private static boolean startView = true;
    private static String confirm;

    @PostMapping("/")
    public String login() {
        return "redirect:/";
    }

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

    @PostMapping("/complete/{id}")
    public String doneTask(@PathVariable("id") Long taskId, @RequestParam("userid") String userid) {
        try {
            confirm = taskService.completeTask(userid, taskId);
        } catch (FeignException.NotFound e) {
            responseMessage = e.getMessage();
        }
        return "redirect:/myTasks?userid=" + userid;
    }

    @PostMapping("/rollback/{id}")
    public String cancelTask(@PathVariable("id") Long taskId, @RequestParam("userid") String userid)  {
        try {
            confirm = taskService.rollbackTask(userid, taskId).getBody();
        } catch (FeignException.NotFound e) {
            responseMessage = e.getMessage();
        }
        return "redirect:/myTasks?userid=" + userid;
    }


    @PostMapping("/take/{id}")
    public String takeMeTask(@PathVariable("id") Long taskId, @RequestParam("userid") String userid) {
        ResponseEntity<String> responseEntity = ResponseEntity.ok().body(null);
        try {
            responseEntity = taskService.takeTask(taskId, userid);
            confirm = responseEntity.getBody();
        } catch (FeignException.InternalServerError ex) {
            responseMessage = responseEntity.getBody();
        } catch (FeignException.NotFound ex) {
            responseMessage = ex.contentUTF8();
        } catch (FeignException.BadRequest ex) {
            responseMessage = ex.contentUTF8();
        }
        return "redirect:/";
    }

    @LogMethod
    @GetMapping("/myModules/{taskId}")
    public String modules(@PathVariable("taskId") Long taskId, @RequestParam("userid") String userId, Model model) {
        List<Module> modules = taskService.getModuleByTaskid(taskId);
        System.out.println(modules);
        model.addAttribute("confirm", confirm);
        model.addAttribute("errorInfo", responseMessage);
        responseMessage = null;
        confirm = null;
        model.addAttribute("username", userId);
        model.addAttribute("modules", modules);
        return "modules.html";
    }

    @LogMethod
    @GetMapping("/myTasks")
    public String myTasks(@RequestParam("userid") String userId, Model model) {
        List<Task> my = taskService.showMyTasks(userId);
        System.out.println(my);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Получаем имя пользователя из объекта аутентификации
        String username = authentication.getName();
        // Добавляем имя пользователя в модель для передачи его в представление
        model.addAttribute("errorInfo", responseMessage);
        responseMessage = null;
        model.addAttribute("username", username);
        model.addAttribute("tasks", my);
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
        List<Task> temp = new ArrayList<>();
        try {
            if (startView) {
                temp = taskService.pullTasks();
                startView = false;
            } else {
                temp = taskService.getTasks(LocalTime.now());
            }
        } catch (WebClientResponseException.ServiceUnavailable | WebClientRequestException |
                 feign.RetryableException ex) {
            taskservice_StatusCode = "503 SERVICE_UNAVAILABLE";
        }
//        tasks = taskApi.getTasks().getBody();
        System.out.println(temp);
        model.addAttribute("message", responseCode);
        model.addAttribute("confirm", confirm);
        model.addAttribute("errorInfo", responseMessage);
        model.addAttribute("taskservice", taskservice_StatusCode);
        responseMessage = null;
        taskservice_StatusCode = null;
        confirm = null;
        model.addAttribute("tasks", temp);
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
