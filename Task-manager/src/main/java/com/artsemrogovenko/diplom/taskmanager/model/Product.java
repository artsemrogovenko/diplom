package com.artsemrogovenko.diplom.taskmanager.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Entity
@NoArgsConstructor
public class Product {
    @Id
    @Column(name = "id", nullable = false, unique = true) // Указываем, что это первичный ключ и он не может быть пустым
    private String contractNumber;
    private String type;   //mr mrl
    private Integer load; // грузоподъесность
    private String color; // цвет
    private Integer floors;  // остановок
    private Boolean done; // готовность
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "product_task", // Имя таблицы для связи между Product и Template
            joinColumns = @JoinColumn(name = "product_contractNumber"), // Столбец для связи с Product
            inverseJoinColumns = @JoinColumn(name = "task_id") // Столбец для связи с Template
    )
    private List<Task> tasks = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (this.done == null) {
            this.done = false;
        }
    }

}
