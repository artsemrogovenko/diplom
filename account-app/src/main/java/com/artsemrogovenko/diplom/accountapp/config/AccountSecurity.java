package com.artsemrogovenko.diplom.accountapp.config;

import com.artsemrogovenko.diplom.accountapp.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
@EnableWebSecurity
public class AccountSecurity {
    @Autowired
    private AccountService accountService;

    /**
     * При этом конфигурация JDBC будет использовать базу данных , запущенную по адресу из настроек приложения.
     * Приложение будет автоматически использовать схему по умолчанию для создания таблиц пользователей и их ролей.
     *
     * @param auth
     * @throws Exception
     */
    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService).passwordEncoder(passwordEncoder());
        auth.inMemoryAuthentication()
                .withUser("admin").password(passwordEncoder().encode("admin")).roles("ADMIN");
    }

    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }

}
