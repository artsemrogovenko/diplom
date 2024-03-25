package com.artsemrogovenko.diplom.storage.model;

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
    @Id
    private String contractNumber;
    @ManyToMany(mappedBy = "contractNumbers")
    private List<Deficit> deficitList;

    public ContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }
}
