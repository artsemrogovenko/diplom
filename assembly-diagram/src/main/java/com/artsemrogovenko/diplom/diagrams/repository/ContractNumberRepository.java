package com.artsemrogovenko.diplom.diagrams.repository;

import com.artsemrogovenko.diplom.diagrams.model.ContractNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractNumberRepository extends JpaRepository<ContractNumber, String> {
    ContractNumber findByNumber(String n);
}
