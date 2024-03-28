package com.artsemrogovenko.diplom.storage.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    @ManyToMany(mappedBy = "contractNumbers")
    private List<Deficit> deficitList;

    public ContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    @Override
    public String toString() {
        return "ContractNumber{" +
                "contractNumber='" + contractNumber + '\'' +
                '}';
    }
}
