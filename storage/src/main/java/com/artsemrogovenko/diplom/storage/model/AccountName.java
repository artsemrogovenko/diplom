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
public class AccountName {
    @Id
    private String name;
    @JsonIgnore
    @ManyToMany(mappedBy = "accountNames")
    private List<Deficit> deficitList;

    public AccountName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AccountName{" +
                "name='" + name + '\'' +
                '}';
    }
}
