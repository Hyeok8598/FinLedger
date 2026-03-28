package com.finledger.finledger.account.api;

import com.finledger.finledger.account.AccountRepository;
import com.finledger.finledger.account.AccountService;
import com.finledger.finledger.account.api.dto.AmountRequest;
import com.finledger.finledger.account.api.dto.CreateAccountRequest;
import com.finledger.finledger.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ApiResponse<Long> create(@RequestBody CreateAccountRequest request) {
        return ApiResponse.success(
          accountService.createAccount(
                  request.getOwnerName(),
                  request.getInitialBalance())
        );
    }

    @GetMapping("/{id}/balance")
    public ApiResponse<BigDecimal> balance(@PathVariable Long id) {
        return ApiResponse.success(
                accountService.getBalance(id)
        );
    }

    @PostMapping("/{id}/deposit")
    public ApiResponse<Void> deposit(@PathVariable Long id,
                        @RequestBody AmountRequest request) {
        accountService.deposit(id, request.getAmount());
        return ApiResponse.success();
    }

    @PostMapping("/{id}/withdraw")
    public ApiResponse<Void> withdraw(@PathVariable Long id,
                         @RequestBody AmountRequest request) {
        accountService.withdraw(id, request.getAmount());
        return ApiResponse.success();
    }
}