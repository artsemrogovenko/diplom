package com.artsemrogovenko.diplom.taskmanager.controller;

import com.artsemrogovenko.diplom.taskmanager.aop.TrackUserAction ;

import com.artsemrogovenko.diplom.taskmanager.model.Task;
import com.artsemrogovenko.diplom.taskmanager.services.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер работы с задачами.
 */
@RestController
@RequestMapping("/task")
@AllArgsConstructor
public class TaskController {
    /**
     * Сервис работы с задачами.
     */
    private final TaskService taskService;


    /**
     * Получение всех задач.
     * @return ответ со списком задач.
     */

    @TrackUserAction
    @GetMapping
    public ResponseEntity<List<Task>> getTasks(){
        return ResponseEntity.ok().body(taskService.getAllTask());
    }

    /**
     * Получение определенной задачи.
     * @param id идентификатор задачи.
     * @return ответ с задачей.
     */

    @TrackUserAction
    @GetMapping("{id}")
    public ResponseEntity<Task> getTask(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(taskService.getTaskById(id));
    }
    /**
     * Резервирование задачи.
     * @param id идентификатор задачи.
     * @return ответ с подтверждением успешного выполнения.
     */

    @TrackUserAction
    @PostMapping("{id}/reserve")
    public ResponseEntity<Void> reserveAmount(@PathVariable("id") Long id,@RequestBody String userId){
        taskService.reservedTask(id,userId);
        return ResponseEntity.ok().body(null);
    }

    /**
     *
     * @param id идентификатор задачи
     * @param userId идентификатор исполнителя
     */
    @TrackUserAction
    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> completeTask(@PathVariable Long id, @RequestBody String userId){
        taskService.completedTask(id,userId);
        return ResponseEntity.ok().body(null);
    }

    /**
     * Откат резервации задачи.
     * @param id идентификатор задачи.
     * @return ответ с подтверждением успешного выполнения.
     */

    @TrackUserAction
    @PostMapping("{id}/reserve/rollback")
    public ResponseEntity<Void> rollbackReserveAmount(@PathVariable("id") Long id, @RequestBody String userId){
        System.out.println("Контроллер отмены выполнения");
        taskService.rollbackReservedTask(id,userId);
        return ResponseEntity.ok().body(null);
    }

}
