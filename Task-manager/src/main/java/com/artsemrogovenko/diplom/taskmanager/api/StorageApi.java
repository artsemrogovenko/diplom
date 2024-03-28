package com.artsemrogovenko.diplom.taskmanager.api;

import com.artsemrogovenko.diplom.taskmanager.aop.TrackUserAction;
import com.artsemrogovenko.diplom.taskmanager.dto.ComponentRequest;
import com.artsemrogovenko.diplom.taskmanager.dto.ComponentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "storage-server")
public interface StorageApi {
    @TrackUserAction
    @PostMapping("/component/megaImport")
    ResponseEntity<String> saveComponents(@RequestBody List<ComponentRequest> components);

    @TrackUserAction
    @PostMapping("/component/reserve")
    ResponseEntity<List<ComponentResponse>> reserve(
            @RequestBody List<ComponentRequest> requests, @RequestParam String user, @RequestParam String contractNumber, @RequestParam Long taskId);

}
