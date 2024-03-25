package com.artsemrogovenko.diplom.accountapp.api;

import com.artsemrogovenko.diplom.accountapp.models.ComponentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "storage-server")
public interface StorageApi {

    @PostMapping("/component/megaImport")
    ResponseEntity<String> saveComponents(@RequestBody List<ComponentRequest> components);


}
