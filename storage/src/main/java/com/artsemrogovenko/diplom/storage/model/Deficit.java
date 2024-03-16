package com.artsemrogovenko.diplom.storage.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
public class Deficit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "VARCHAR(100)")
    private String factoryNumber;    // заводской номер
    @Column(columnDefinition = "VARCHAR(100)")
    private String model;       // модификация
    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String name;     // название
    @Column(columnDefinition = "INT", nullable = false)
    private int quantity;    // какое количество
    @Column(columnDefinition = "VARCHAR(20)", nullable = false)
    private String unit;     // единица измерения
    @Column(columnDefinition = "VARCHAR(255)")
    private String description;  // тут можно указать например цвет
    @Column(columnDefinition = "BOOL", nullable = false)
    private boolean refill;  // можно обьединить?
    @ManyToMany
    @JoinTable(
            name = "deficit_contract_numbers", // Имя связующей таблицы для Deficit и ContractNumber
            joinColumns = @JoinColumn(name = "deficit_id"), // Столбец с внешним ключом для Deficit
            inverseJoinColumns = @JoinColumn(name = "contract_number_id") // Столбец с внешним ключом для ContractNumber
    )
    private Set<ContractNumber> contractNumbers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "deficit_account_names", // Имя связующей таблицы для Deficit и AccountName
            joinColumns = @JoinColumn(name = "deficit_id"), // Столбец с внешним ключом для Deficit
            inverseJoinColumns = @JoinColumn(name = "account_name_id") // Столбец с внешним ключом для AccountName
    )
    private Set<AccountName> accountNames = new HashSet<>();
}
