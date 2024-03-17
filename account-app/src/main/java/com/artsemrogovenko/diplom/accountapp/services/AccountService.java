package com.artsemrogovenko.diplom.accountapp.services;

import com.artsemrogovenko.diplom.accountapp.models.Account;
import com.artsemrogovenko.diplom.accountapp.models.Task;
import com.artsemrogovenko.diplom.accountapp.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;

    public boolean saveUser(Account user) {
        Optional<Account> userFromDB = accountRepository.findById(user.getName());
        System.out.println(user);
        if (userFromDB.isPresent()) {
            return false;
        }
        if (user.getRoles().isEmpty()) {
            user.setRoles(List.of("ROLE_USER"));
        }
        accountRepository.save(user);
        return true;

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByName(username);
        if (account == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        List<SimpleGrantedAuthority> authorities = account.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());

        return User.withUsername(account.getName())
                .password(account.getPassword())
                .authorities(authorities)
                .build();
    }

    public Account findUserById(String name) {
        return accountRepository.findById(name).orElse(null);
    }

    public ResponseEntity<?> getTaskByOwner(String userId, String description) {
        Optional<Task> taskOptional = accountRepository.findById(userId).get().getTasks().stream()
                .filter(task -> task.getDescription().equals(description)).findFirst();


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
        return getAllAccounts().stream().flatMap(account -> account.getTasks().stream()).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
