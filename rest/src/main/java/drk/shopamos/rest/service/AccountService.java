package drk.shopamos.rest.service;

import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;

    public Account loadUserByUsername(String username) {
        return accountRepository
                .findByEmail(username)
                .orElseThrow(
                        () ->
                                new UsernameNotFoundException(
                                        String.format("%s not found", username)));
    }
}
