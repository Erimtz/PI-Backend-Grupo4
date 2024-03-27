package com.gym.controllers;

import com.gym.dto.response.AccountPurchaseDTO;
import com.gym.entities.Account;
import com.gym.security.entities.UserEntity;
import com.gym.services.impl.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @Operation(summary = "Obtener todas las cuentas con sus compras")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuentas con sus compras obtenidas con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Account.class))}),
            @ApiResponse(responseCode = "500", description = "Ocurrió un error al procesar la solicitud.",content = @Content)})
    @GetMapping("/get-all-with-purchases")
    public ResponseEntity<?> getAllAccountsWithPurchasesDTO() {
        try {
            List<AccountPurchaseDTO> accountPurchaseDTOs = accountService.getAllAccountsWithPurchasesDTO();
            return ResponseEntity.ok(accountPurchaseDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error al procesar la solicitud.");
        }
    }
}
