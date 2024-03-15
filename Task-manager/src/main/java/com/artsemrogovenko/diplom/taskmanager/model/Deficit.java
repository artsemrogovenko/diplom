package com.artsemrogovenko.diplom.taskmanager.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
/**
 * класс для просмотра недостающего количества
 */
public class Deficit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String contractNumber;
    @OneToOne
    private Component requiredComponent;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Product product; // что нехватает для изготовления
}
