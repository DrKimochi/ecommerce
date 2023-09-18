package drk.shopamos.rest.service;

import static drk.shopamos.rest.config.MessageProvider.MSG_ENTITY_NOT_FOUND;

import drk.shopamos.rest.config.MessageProvider;
import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.repository.AccountRepository;
import drk.shopamos.rest.service.exception.EntityExistsException;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final MessageProvider msgProvider;

    public Account loadUserByUsername(String username) {
        return accountRepository
                .findByEmail(username)
                .orElseThrow(
                        () ->
                                new UsernameNotFoundException(
                                        msgProvider.getMessage(MSG_ENTITY_NOT_FOUND, username)));
    }

    public void createAccount(Account account) {
        if (accountRepository.existsByEmail(account.getEmail())) {
            throw new EntityExistsException(msgProvider, account.getEmail());
        }

        accountRepository.save(account);
    }
}
