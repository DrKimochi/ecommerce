package drk.shopamos.rest.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.repository.AccountRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock AccountRepository accountRepository;
    @InjectMocks AccountService testee;
    private String email;
    private Account account;

    @BeforeEach
    void setup() {
        email = "username@domain.com";
        account = new Account();
        account.setEmail(email);
    }

    @Test
    @DisplayName("loadUserByUsername - finds the account by email from account repository")
    void loadUserByUsername_findsUserByEmail() {
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(account));

        Account foundAccount = testee.loadUserByUsername(email);
        assertThat(account, is(foundAccount));
    }

    @Test
    @DisplayName("loadUserByUsername - throws exception when it cannot find the account by email")
    void loadUserByUsername_throwsExceptionWhenNotFound() {
        when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> testee.loadUserByUsername(email));
    }
}
