package com.artsemrogovenko.diplom.storage.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class ContractNumber {

    @Id
    private String contractNumber;
    @ManyToMany(mappedBy = "contractNumbers")
    private List<Deficit> deficitList;

    public ContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }
}
