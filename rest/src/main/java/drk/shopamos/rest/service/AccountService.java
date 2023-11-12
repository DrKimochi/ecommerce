package drk.shopamos.rest.service;

import static drk.shopamos.rest.config.MessageProvider.MSG_EXISTS_EMAIL;

import drk.shopamos.rest.config.MessageProvider;
import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.repository.AccountRepository;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;

@Service
public class AccountService extends BaseService implements UserDetailsService {
    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(
            PasswordEncoder passwordEncoder,
            AccountRepository accountRepository,
            MessageProvider messageProvider,
            Clock clock) {
        super(messageProvider, clock);
        this.repository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Account loadUserByUsername(String username) {
        return repository.findByEmail(username).orElseThrow(aUsernameNotFoundException(username));
    }

    public Account createAccount(Account account) {
        String email = account.getEmail();
        if (repository.findByEmail(email).isPresent()) {
            throw anEntityExistsException(MSG_EXISTS_EMAIL, email).get();
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
        Account account = getAccount(id);
        repository.delete(account);
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
}
