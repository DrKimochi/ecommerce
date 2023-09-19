package drk.shopamos.rest.controller;

import drk.shopamos.rest.controller.mapper.AccountMapper;
import drk.shopamos.rest.controller.request.AccountRequest;
import drk.shopamos.rest.service.AccountService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/accounts")
public class AccountController {
    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> createAccount(@Valid @RequestBody AccountRequest accountRequest) {
        accountService.createAccount(accountMapper.map(accountRequest));
        return ResponseEntity.ok().build();
    }
}
