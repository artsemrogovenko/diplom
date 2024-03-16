package com.artsemrogovenko.diplom.specification.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.artsemrogovenko.diplom.specification.model.DiagramDescription;
@FeignClient(name = "diagram-server")
public interface AssemblyApi {
    @PostMapping("/rest/getUrlsheme")
    ResponseEntity<String> requestSheme(@RequestBody DiagramDescription diagramDescription);
}
