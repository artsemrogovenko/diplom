package com.artsemrogovenko.diplom.storage.repositories;

import com.artsemrogovenko.diplom.storage.model.AccountName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface AccountRepository extends JpaRepository<AccountName, String> {

}
