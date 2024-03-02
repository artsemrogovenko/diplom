package com.artsemrogovenko.diplom.storage;

import com.artsemrogovenko.diplom.storage.dto.ComponentRequest;
import com.artsemrogovenko.diplom.storage.dto.mymapper.ComponentMapper;
import com.artsemrogovenko.diplom.storage.model.Component;
import com.artsemrogovenko.diplom.storage.repositories.ComponentRepository;
import com.artsemrogovenko.diplom.storage.service.ComponentService;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(StorageApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(ComponentService componentService) {
        return args -> {
            ComponentRequest component1 = ComponentRequest.builder().factoryNumber("79436").name("НШВИ 0.75-8").quantity(4).unit("шт").refill(true).build();
            ComponentRequest component2 = ComponentRequest.builder().factoryNumber("00-00012187").name("МКЭШнг(А)-LS 7х0.75 500В").quantity(5).unit("м").refill(false).build();

            componentService.createComponent(component1);
            componentService.createComponent(component2);
        };
    }
}
