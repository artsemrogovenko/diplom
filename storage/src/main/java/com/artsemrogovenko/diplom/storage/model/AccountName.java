package com.artsemrogovenko.diplom.storage.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
@Data
@Entity
@NoArgsConstructor
public class AccountName {
    @Id
    private String name;

    @ManyToMany(mappedBy = "accountNames")
    private List<Deficit> deficitList;

    public AccountName(String name) {
        this.name = name;
    }
}
