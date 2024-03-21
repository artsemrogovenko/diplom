package com.artsemrogovenko.diplom.accountapp.api;

import com.artsemrogovenko.diplom.accountapp.models.Task;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "task-server")
public interface TaskApi {

    @GetMapping("/task")
    ResponseEntity<List<Task>> getTasks();
    @PostMapping("/task/{id}/reserve")
    public ResponseEntity<String> reserveAmount(@PathVariable("id") Long id,@RequestBody String userId);
    @PostMapping("/task/{id}/complete")
    public ResponseEntity<String> completeTask(@PathVariable("id") Long id, @RequestBody String userId);
    @PostMapping("/task/{id}/reserve/rollback")
    ResponseEntity<String> rollbackReserveAmount(@PathVariable("id") Long id, @RequestBody String userId);


}

