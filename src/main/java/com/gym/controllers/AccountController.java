package com.gym.controllers;

import com.gym.dto.AccountDto;
import com.gym.dto.CreateAccountDto;
import com.gym.entities.Account;
import com.gym.entities.ERank;
import com.gym.entities.Rank;
import com.gym.security.entities.UserEntity;
import com.gym.service.AccountService;
import com.gym.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/account")
public class AccountController {

    private AccountService accountService;

    private UserService userService;

    public AccountController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountDto createAccountDto) {
        // busca el usuario
        UserEntity newUser = userService.findUserByUsername(createAccountDto.getUsername());

        if (newUser != null) {
            Rank rank = new Rank(1L, ERank.BRONZE);

            Account newAccount = accountService.createAccount(newUser, new BigDecimal(0), rank);

            return ResponseEntity.ok(newAccount);
        } else {
            return ResponseEntity.badRequest().body("No se pudo crear el usuario y la cuenta asociada.");
        }
    }

    @GetMapping("/details/{username}")
    public ResponseEntity<AccountDto> getAccountDetails(@PathVariable String username) {
        // Obtener los detalles de la cuenta
        AccountDto accountDetails = accountService.getAccountDetails(username);

        return ResponseEntity.ok(accountDetails);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUserWithAccount(@PathVariable Long userId) {
        // Eliminar la cuenta asociada al usuario
        accountService.deleteAccountByUserId(userId);

        // Luego, eliminar a el usuario
        userService.deleteUserById(userId);

        return ResponseEntity.ok("User with ID " + userId + " and associated account deleted successfully");
    }
}
