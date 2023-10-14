package drk.shopamos.rest.controller;

import drk.shopamos.rest.controller.mapper.AccountMapper;
import drk.shopamos.rest.controller.request.AccountRequest;
import drk.shopamos.rest.controller.response.AccountResponse;
import drk.shopamos.rest.controller.violation.CustomerSelfPromoteViolation;
import drk.shopamos.rest.controller.violation.CustomerTargetingAnotherViolation;
import drk.shopamos.rest.controller.violation.PrincipalDataViolationChain;
import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.service.AccountService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/accounts")
public class AccountController {
    // TODO: GET /accounts/{Id}. Customers can get themselves
    // TODO: GET /accounts?queryPrams. Only available for admins
    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountRequest accountRequest) {
        Account account = accountService.createAccount(accountMapper.map(accountRequest));
        return ResponseEntity.ok(accountMapper.map(account));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    public ResponseEntity<AccountResponse> updateAccount(
            @PathVariable(name = "id") Integer id,
            @Valid @RequestBody AccountRequest accountRequest) {
        Account account = accountMapper.map(accountRequest, id);

        new PrincipalDataViolationChain<>(account)
                .add(new CustomerSelfPromoteViolation())
                .add(new CustomerTargetingAnotherViolation())
                .verify();

        account = accountService.updateAccount(account);
        return ResponseEntity.ok(accountMapper.map(account));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    public ResponseEntity<Void> deleteAccount(@PathVariable(name = "id") Integer id) {
        Account account = accountMapper.map(null, id);

        new PrincipalDataViolationChain<>(account)
                .add(new CustomerTargetingAnotherViolation())
                .verify();

        accountService.deleteAccount(id);
        return ResponseEntity.ok().build();
    }
}
