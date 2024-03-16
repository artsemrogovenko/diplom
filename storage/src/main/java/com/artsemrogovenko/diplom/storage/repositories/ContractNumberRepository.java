package com.artsemrogovenko.diplom.storage.repositories;

import com.artsemrogovenko.diplom.storage.model.ContractNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractNumberRepository extends JpaRepository<ContractNumber, String> {
}
