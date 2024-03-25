package com.artsemrogovenko.diplom.accountapp.repositories;

import com.artsemrogovenko.diplom.accountapp.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с пользователями.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    Account findByName(String userId);
}
