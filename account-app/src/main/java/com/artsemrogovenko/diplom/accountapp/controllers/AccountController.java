package com.artsemrogovenko.diplom.accountapp.controllers;

import com.artsemrogovenko.diplom.accountapp.models.Account;
import com.artsemrogovenko.diplom.accountapp.services.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    @GetMapping()
    public ResponseEntity<List<Account>> getAccounts() {
        return ResponseEntity.ok().body(accountService.getAllAccounts());
    }

    @GetMapping("/getByDescription/{descr}")
    public ResponseEntity<?> getTask(@RequestBody String ownerId, @PathVariable String description) {
        return accountService.getTaskByOwner(ownerId, description);
    }


}
