package com.artsemrogovenko.diplom.specification.controller;

import com.artsemrogovenko.diplom.specification.dto.ModuleData;
import com.artsemrogovenko.diplom.specification.dto.ModuleRequest;
import com.artsemrogovenko.diplom.specification.dto.ModuleResponse;
import com.artsemrogovenko.diplom.specification.httprequest.MyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {
    private String response;
    private boolean pushrequest = false;
    private boolean startView = true;

    private List<ModuleResponse> sessionList;

    @GetMapping("/")
    public String main(Model model) throws Exception {
        if (startView) {
            pullList();
            startView = false;
        }
        model.addAttribute("newModule", new ModuleRequest());
        model.addAttribute("modules", sessionList);
        model.addAttribute("message", response);
        response=null;
        return "index";
    }

    private String myserializer(Object obj) {
        try {
            // Создаем объект ObjectMapper для сериализации объекта ModuleRequest в JSON
            ObjectMapper objectMapper = new ObjectMapper();
            // Сериализуем объект ModuleRequest в JSON
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/createModule")
    public String create(ModuleRequest moduleRequest, Model model) {
        try {
            // Сериализуем данные в JSON
            String serializedData =myserializer(moduleRequest);
            // Вызываем метод convertResponse с объектом запроса, JSON-строкой и ожидаемым типом ответа
            ResponseEntity<ModuleResponse> responseEntity = MyRequest.convertResponse(new HttpPost("http://localhost:8082/module"), serializedData, ModuleResponse.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                pullList();
            }
            pushrequest = false;
            response = responseEntity.getStatusCode().toString();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(moduleRequest.toString());
        return "redirect:/";
    }

    @PostMapping("/updateModule")
    public String update(ModuleResponse moduleResponse, Model model) {
//        moduleService.updateModule(moduleResponse);
        System.out.println(moduleResponse.toString());
        return "redirect:/";
    }

    @SneakyThrows
    private void pullList() {
        sessionList = MyRequest.convertResponseList("http://localhost:8082/module", ModuleResponse.class).getBody();
    }



}
