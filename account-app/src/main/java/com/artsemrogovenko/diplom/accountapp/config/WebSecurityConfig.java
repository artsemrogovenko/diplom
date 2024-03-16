package com.artsemrogovenko.diplom.accountapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig   {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())

                .authorizeRequests(authorize -> authorize
//                        .requestMatchers("/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                )
                .logout(logout -> logout
//                        .logoutSuccessUrl("/login").permitAll()
                                .logoutSuccessUrl("/").permitAll()
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/login?error=У%20вас%20нет%20доступа.%20Введите%20данные%20администратора")
                );
        return  http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder().username("user").password("123").roles("USER").build();
        UserDetails admin = User.withDefaultPasswordEncoder().username("admin").password("admin").roles("ADMIN").build();

        return new InMemoryUserDetailsManager(user,admin);
    }
}