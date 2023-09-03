package drk.shopamos.rest.service;

import drk.shopamos.rest.config.JwtTokenHelper;
import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authManager;
    private final AccountRepository accountRepository;
    private final JwtTokenHelper jwtTokenHelper;

    public String login(String username, String password) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        Account account = accountRepository.findByEmail(username).orElseThrow();
        return jwtTokenHelper.generateToken(account);
    }
}
