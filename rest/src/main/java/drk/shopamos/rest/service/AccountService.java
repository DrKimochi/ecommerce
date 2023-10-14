package drk.shopamos.rest.service;

import static drk.shopamos.rest.config.MessageProvider.MSG_EXISTS_EMAIL;
import static drk.shopamos.rest.config.MessageProvider.MSG_NOT_FOUND_ID;
import static drk.shopamos.rest.config.MessageProvider.MSG_NOT_FOUND_USER;

import drk.shopamos.rest.config.MessageProvider;
import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.repository.AccountRepository;
import drk.shopamos.rest.service.exception.EntityExistsException;
import drk.shopamos.rest.service.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final MessageProvider msgProvider;
    private final PasswordEncoder passwordEncoder;

    public Account loadUserByUsername(String username) {
        return accountRepository
                .findByEmail(username)
                .orElseThrow(aUsernameNotFoundException(username));
    }

    public Account createAccount(Account account) {
        validateEmailDoesNotExist(account.getEmail());
        encodePassword(account);
        return accountRepository.save(account);
    }

    public Account updateAccount(Account account) {
        validateIdExists(account.getId());
        encodePassword(account);
        return accountRepository.save(account);
    }

    public void deleteAccount(Integer id) {
        validateIdExists(id);
        accountRepository.deleteById(id);
    }

    public Account getAccount(Integer id) {
        return accountRepository.findById(id).orElseThrow(anEntityNotFoundException(id));
    }

    private void validateEmailDoesNotExist(String email) {
        if (accountRepository.existsByEmail(email)) {
            throw new EntityExistsException(msgProvider.getMessage(MSG_EXISTS_EMAIL, email));
        }
    }

    private void validateIdExists(Integer id) {
        if (!accountRepository.existsById(id)) {
            throw new EntityNotFoundException(msgProvider.getMessage(MSG_NOT_FOUND_ID, id));
        }
    }

    private Supplier<EntityNotFoundException> anEntityNotFoundException(Integer id) {
        return () -> new EntityNotFoundException(msgProvider.getMessage(MSG_NOT_FOUND_ID, id));
    }

    private Supplier<UsernameNotFoundException> aUsernameNotFoundException(String username) {
        return () ->
                new UsernameNotFoundException(msgProvider.getMessage(MSG_NOT_FOUND_USER, username));
    }

    private void encodePassword(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
    }
}
