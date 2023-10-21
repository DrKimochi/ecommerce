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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/accounts")
public class AccountController {
    private final AccountService service;
    private final AccountMapper mapper;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountRequest accountRequest) {
        Account account = service.createAccount(mapper.map(accountRequest));
        return ResponseEntity.ok(mapper.map(account));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAccount(
            @PathVariable(name = "id") Integer id,
            @Valid @RequestBody AccountRequest accountRequest) {
        Account account = mapper.map(accountRequest, id);

        new PrincipalDataViolationChain<>(account)
                .add(new CustomerSelfPromoteViolation())
                .add(new CustomerTargetingAnotherViolation())
                .verify();

        account = service.updateAccount(account);
        return ResponseEntity.ok(mapper.map(account));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable(name = "id") Integer id) {
        Account account = mapper.map(null, id);

        new PrincipalDataViolationChain<>(account)
                .add(new CustomerTargetingAnotherViolation())
                .verify();

        service.deleteAccount(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable(name = "id") Integer id) {
        Account account = mapper.map(null, id);

        new PrincipalDataViolationChain<>(account)
                .add(new CustomerTargetingAnotherViolation())
                .verify();

        account = service.getAccount(id);
        return ResponseEntity.ok(mapper.map(account));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<AccountResponse>> getAccounts(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "isAdmin", required = false) Boolean isAdmin,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {

        List<Account> foundAccounts = service.getAccounts(name, email, isAdmin, isActive);
        List<AccountResponse> response = foundAccounts.stream().map(mapper::map).toList();
        return ResponseEntity.ok(response);
    }
}
