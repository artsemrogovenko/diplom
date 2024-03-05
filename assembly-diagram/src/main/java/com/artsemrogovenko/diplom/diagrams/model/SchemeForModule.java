package com.artsemrogovenko.diplom.diagrams.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Entity
@Table(name = "File_Data")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchemeForModule  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany
    @JoinTable(name = "scheme_contract_numbers",
            joinColumns = @JoinColumn(name = "scheme_id"),
            inverseJoinColumns = @JoinColumn(name = "contract_number_id"))
    private List<ContractNumber> contractNumbers;

    private String filePath;
    private String moduleName;
    private String modification;
    private String versionAssembly;

}
