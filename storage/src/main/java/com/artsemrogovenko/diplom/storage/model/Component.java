package com.artsemrogovenko.diplom.storage.model;

import com.artsemrogovenko.diplom.storage.dto.ComponentData;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
public class Component implements ComponentData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "VARCHAR(100)")
    private String factoryNumber;       // заводской номер
    @Column(columnDefinition = "VARCHAR(100)")
    private String model;       // модификация
    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String name;     // имя
    @Column(columnDefinition = "INT", nullable = false)
    private Integer quantity;    // какое количество
    @Column(columnDefinition = "VARCHAR(20)", nullable = false)
    private String unit;     // единица измерения
    @Column(columnDefinition = "VARCHAR(255)")
    private String description;  // тут можно указать например цвет
    @Column(columnDefinition = "BOOL", nullable = false)
    private boolean refill;  // можно объединить?

    @Override
    public Boolean getRefill() {
        return refill;
    }

}