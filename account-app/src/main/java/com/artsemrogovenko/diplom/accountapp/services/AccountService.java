package com.artsemrogovenko.diplom.accountapp.services;

import com.artsemrogovenko.diplom.accountapp.models.Account;
import com.artsemrogovenko.diplom.accountapp.models.Task;
import com.artsemrogovenko.diplom.accountapp.models.Transaction;
import com.artsemrogovenko.diplom.accountapp.models.exceptions.DuplicateExeption;
import com.artsemrogovenko.diplom.accountapp.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    public void saveUser(Account user) {
        accountRepository.save(user);
    }

    public Account findUserById(String name) {
        return accountRepository.findById(name).orElse(null);
    }
    public ResponseEntity<?> getTaskByOwner(String userId, String description) {
        Optional<Task> taskOptional = accountRepository.findById(userId).get().getTasks().stream()
                .filter(task -> task.getDescription().equals(description)).findFirst();

        System.out.println(taskOptional);

        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            return ResponseEntity.ok(task); // Вернуть найденный объект Task
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Задача не найдена");
        }
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }


    /**
     * Получение всех задач от владельцев.
     */
    public List<Task> getAllTasks() {
        return  getAllAccounts().stream().flatMap(account -> account.getTasks().stream()).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
