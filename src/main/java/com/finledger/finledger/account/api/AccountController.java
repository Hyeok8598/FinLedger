package com.finledger.finledger.account.api;

import com.finledger.finledger.account.AccountService;
import com.finledger.finledger.account.api.dto.AmountRequest;
import com.finledger.finledger.account.api.dto.CreateAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public Long create(@RequestBody CreateAccountRequest request) {
        return accountService.createAccount(
                request.getOwnerName(),
                request.getInitialBalance()
        );
    }

    @GetMapping("/{id}/balance")
    public BigDecimal balance(@PathVariable Long id) {
        return accountService.getBalance(id);
    }

    @PostMapping("/{id}/deposit")
    public void deposit(@PathVariable Long id,
                        @RequestBody AmountRequest request) {
        accountService.deposit(id, request.getAmount());
    }

    @PostMapping("/{id}/withdraw")
    public void withdraw(@PathVariable Long id,
                         @RequestBody AmountRequest request) {
        accountService.withdraw(id, request.getAmount());
    }
}