package com.artsemrogovenko.diplom.accountapp.controllers;

import com.artsemrogovenko.diplom.accountapp.models.Account;
import com.artsemrogovenko.diplom.accountapp.models.Task;
import com.artsemrogovenko.diplom.accountapp.services.AccountService;
import com.artsemrogovenko.diplom.accountapp.services.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;
    private final AccountService accountService;

    @GetMapping("/sync")
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok().body(accountService.getAllTasks());
    }


    @PostMapping("/rollback/{id}")
    public ResponseEntity<?> rollbackTransaction(@RequestBody String user, @PathVariable Long taskid) {
        return taskService.rollbackTask(user, taskid);
    }


    @PostMapping("/assignTask/{id}")
    public ResponseEntity<String> assignTask(@RequestBody Task task, @RequestParam String userId) {
        return taskService.assign(userId, task);
    }
}
