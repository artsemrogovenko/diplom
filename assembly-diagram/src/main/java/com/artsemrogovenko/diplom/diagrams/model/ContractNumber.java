package com.artsemrogovenko.diplom.diagrams.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Entity
@NoArgsConstructor
public class ContractNumber {
    @ManyToMany(mappedBy = "contractNumbers")
    private List<SchemeForModule> schemeForModules;
    @Id
    @Column(nullable = false, unique = true) // Указываем, что это первичный ключ и он не может быть пустым
    private String number;
    public ContractNumber(String number) {
        this.number = number;
    }

}
