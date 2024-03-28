package com.artsemrogovenko.diplom.specification.api;

import com.artsemrogovenko.diplom.specification.model.DiagramDescription;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
@FeignClient(name = "diagram-server")
public interface AssemblyApi {
    @PostMapping("/rest/getUrlsheme")
    ResponseEntity<String> requestSheme(@RequestBody DiagramDescription diagramDescription);
}
