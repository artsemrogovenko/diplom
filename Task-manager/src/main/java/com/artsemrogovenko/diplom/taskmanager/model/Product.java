package com.artsemrogovenko.diplom.taskmanager.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "product_task", // Имя таблицы для связи между Product и Task
            joinColumns = @JoinColumn(name = "product_contractNumber"), // Столбец для связи с Product
            inverseJoinColumns = @JoinColumn(name = "task_id") // Столбец для связи с Task
    )
    private List<Task> tasks = new ArrayList<>();
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Deficit> deficit = new ArrayList<>(); // что нехватает для изготовления
    @PrePersist
    void onCreate() {
        if (this.done == null) {
            this.done = false;
        }
    }

}
