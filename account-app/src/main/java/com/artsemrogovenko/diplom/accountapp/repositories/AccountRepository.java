package com.artsemrogovenko.diplom.accountapp.repositories;

import com.artsemrogovenko.diplom.accountapp.models.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с пользователями.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

//    @Modifying
//    @Transactional
//    void deleteTaskByTasksDescriptionAndId(String description, Long accountId);

    Account findByName(String userId);
}
