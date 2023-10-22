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

import java.util.List;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository repository;
    private final MessageProvider msgProvider;
    private final PasswordEncoder passwordEncoder;

    public Account loadUserByUsername(String username) {
        return repository.findByEmail(username).orElseThrow(aUsernameNotFoundException(username));
    }

    public Account createAccount(Account account) {
        String email = account.getEmail();
        if (repository.findByEmail(email).isPresent()) {
            throw anEntityExistsException(email).get();
        }
        encodePassword(account);
        return repository.save(account);
    }

    public Account updateAccount(Account account) {
        Integer id = account.getId();
        if (repository.findById(id).isEmpty()) {
            throw anEntityNotFoundException(id).get();
        }
        encodePassword(account);
        return repository.save(account);
    }

    public void deleteAccount(Integer id) {
        if (repository.findById(id).isEmpty()) {
            throw anEntityNotFoundException(id).get();
        }
        repository.deleteById(id);
    }

    public Account getAccount(Integer id) {
        return repository.findById(id).orElseThrow(anEntityNotFoundException(id));
    }

    public List<Account> getAccounts(String name, String email, Boolean isAdmin, Boolean isActive) {
        return repository.findAllByAttributes(name, email, isAdmin, isActive);
    }

    private void encodePassword(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
    }

    private Supplier<EntityExistsException> anEntityExistsException(String email) {
        return () -> new EntityExistsException(msgProvider.getMessage(MSG_EXISTS_EMAIL, email));
    }

    private Supplier<EntityNotFoundException> anEntityNotFoundException(Integer id) {
        return () -> new EntityNotFoundException(msgProvider.getMessage(MSG_NOT_FOUND_ID, id));
    }

    private Supplier<UsernameNotFoundException> aUsernameNotFoundException(String username) {
        return () ->
                new UsernameNotFoundException(msgProvider.getMessage(MSG_NOT_FOUND_USER, username));
    }
}
