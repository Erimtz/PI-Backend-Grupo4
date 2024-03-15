package com.gym.controllers;

import com.gym.dto.response.AccountPurchaseDTO;
import com.gym.services.impl.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

//    public AccountController(AccountService accountService) {
//        this.accountService = accountService;
//    }

    @GetMapping("/get-all-with-purchases")
    public ResponseEntity<List<AccountPurchaseDTO>> getAllAccountsWithPurchasesDTO() {
        List<AccountPurchaseDTO> accountPurchaseDTOs = accountService.getAllAccountsWithPurchasesDTO();
        return ResponseEntity.ok(accountPurchaseDTOs);
    }
}
