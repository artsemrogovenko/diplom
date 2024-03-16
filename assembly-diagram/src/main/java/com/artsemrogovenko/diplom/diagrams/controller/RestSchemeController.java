package com.artsemrogovenko.diplom.diagrams.controller;

import com.artsemrogovenko.diplom.diagrams.dto.DiagramDto;
import com.artsemrogovenko.diplom.diagrams.model.DiagramDescription;
import com.artsemrogovenko.diplom.diagrams.service.DiagramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@Slf4j
@RestController
@RequestMapping("/rest")
@RequiredArgsConstructor
public class RestSchemeController {
private final DiagramService diagramService;
//    @PostMapping
//    public void requestFile(DiagramDescription description, String contractNumber) {
//        String fileHash = FileHashCalculator.calculateHash( file);
//        System.out.println(fileHash);
//        model.addAttribute("fileHash", fileHash);
//        file.transferTo(new File("./myDiagrams/"));
//        log.info("uploaded file " + file.getOriginalFilename());
////        return "redirect:/";
//    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Path> requestFile(@RequestBody DiagramDto diagramDto) {
        log.info("Received  request for scheme: {}", diagramDto.toString());
        return diagramService.getScheme(diagramDto.getModuleName(),diagramDto.getModification(),diagramDto.getVersionAssembly(), diagramDto.getContractNumber());
    }

    @PostMapping("/getUrlsheme")
    public ResponseEntity<String> requestSheme(@RequestBody DiagramDescription diagramDescription) {
        return diagramService.getUrlsheme(diagramDescription);
    }

}