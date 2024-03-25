package com.artsemrogovenko.diplom.taskmanager.api;

import com.artsemrogovenko.diplom.taskmanager.aop.TrackUserAction;
import com.artsemrogovenko.diplom.taskmanager.config.WebClientConfig;
import com.artsemrogovenko.diplom.taskmanager.dto.ComponentRequest;
import com.artsemrogovenko.diplom.taskmanager.dto.ComponentResponse;
import com.artsemrogovenko.diplom.taskmanager.dto.TaskForUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Клиент для сервиса исполнителя задач
 */
@FeignClient(name = "account-service")
public interface AccountApi {
    @TrackUserAction
    @PostMapping("/task/assignTask")
    ResponseEntity<String> assignTask(@RequestBody TaskForUser task, @RequestParam String userId);

}
