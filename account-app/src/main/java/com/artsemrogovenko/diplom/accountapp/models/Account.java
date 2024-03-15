package com.artsemrogovenko.diplom.accountapp.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GeneratedColumn;

import java.util.*;

/**
 * Сущность аккаунта.
 */
@Data
@Entity
public class Account {
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new LinkedList<>();   // список задач

    public Account(String name) {
        this.name = name;
    }
    public Account() {
    }
}
