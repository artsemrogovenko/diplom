package com.artsemrogovenko.diplom.accountapp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Сущность аккаунта.
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();
    @OneToMany(mappedBy = "account",  fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();   // список задач

    public Account(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.name = username;
        this.password = password;
        for (GrantedAuthority authority : authorities) {
            roles.add(authority.getAuthority());
        }
    }

    public void addTask(Task newTask) {
        tasks.add(newTask);
    }
}
