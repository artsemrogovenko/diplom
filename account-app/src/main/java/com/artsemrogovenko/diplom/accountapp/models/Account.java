package com.artsemrogovenko.diplom.accountapp.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

/**
 * Сущность аккаунта.
 */
@Data
@Entity
@NoArgsConstructor
public class Account {
    @Id
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new LinkedList<>();   // список задач


    public Account(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.name = username;
        this.password = password;
        for (GrantedAuthority authority : authorities) {
            roles.add(authority.getAuthority());
        }
    }


}
