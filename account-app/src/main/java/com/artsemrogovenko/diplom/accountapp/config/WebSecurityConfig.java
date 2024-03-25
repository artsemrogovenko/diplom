package com.artsemrogovenko.diplom.accountapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize -> authorize
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/registration/**").hasRole("ADMIN")
                        .requestMatchers("/myTasks/**", "/take/**", "/rollback/**", "/myModules/**", "/complete/**").authenticated()
                        .requestMatchers("/webjars/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.permitAll()
                        .successForwardUrl("/")
                        .failureHandler((request, response, exception) -> {
                            response.sendRedirect("/login?error=true&message=" + URLEncoder.encode("Неверные данные.", StandardCharsets.UTF_8));
                        })

                )
                .logout(logout -> logout.permitAll()
                        .logoutSuccessUrl("/login?logout=true")
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/login?error=true&message=" + URLEncoder.encode("У вас нет доступа.Введите данные администратора", StandardCharsets.UTF_8)))
                .csrf(csrf -> csrf.disable())// Отключаем CSRF
                .headers(headers -> headers.disable());// Разрешаем встраивание фреймов

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder().username("admin").password("admin").roles("ADMIN").build();//
        return new InMemoryUserDetailsManager(admin);
    }


}