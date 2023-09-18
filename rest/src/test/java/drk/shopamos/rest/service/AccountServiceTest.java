package drk.shopamos.rest.service;

import static drk.shopamos.rest.mother.AccountMother.NAMI_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.VIVI_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.buildAccountNami;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import drk.shopamos.rest.config.MessageProvider;
import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.repository.AccountRepository;
import drk.shopamos.rest.service.exception.EntityExistsException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest extends ServiceTest {
    @Mock AccountRepository accountRepository;
    @Mock MessageProvider messageProvider;
    @InjectMocks AccountService testee;

    @Test
    @DisplayName("loadUserByUsername - finds the account by email from account repository")
    void loadUserByUsername_findsUserByEmail() {
        Account nami = buildAccountNami();
        when(accountRepository.findByEmail(NAMI_EMAIL)).thenReturn(Optional.of(nami));
        Account foundAccount = testee.loadUserByUsername(NAMI_EMAIL);
        assertThat(nami, is(foundAccount));
    }

    @Test
    @DisplayName("loadUserByUsername - throws exception when it cannot find the account by email")
    void loadUserByUsername_throwsExceptionWhenNotFound() {
        when(accountRepository.findByEmail(VIVI_EMAIL)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> testee.loadUserByUsername(VIVI_EMAIL));
        verify(messageProvider).getMessage(MSG_ENTITY_NOT_FOUND, VIVI_EMAIL);
    }

    @Test
    @DisplayName("createAccount - throws exception when account already exists")
    void createAccount_throwsExceptionWhenAccountAlreadyExists() {
        Account nami = buildAccountNami();
        when(accountRepository.existsByEmail(NAMI_EMAIL)).thenReturn(true);
        assertThrows(EntityExistsException.class, () -> testee.createAccount(nami));
        verify(messageProvider).getMessage(MSG_ENTITY_EXISTS, NAMI_EMAIL);
        verify(accountRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("createAccount - saves account when it doesnt exist yet")
    void createAccount_savesWhenAccountDoesntExistYet() {
        Account nami = buildAccountNami();
        when(accountRepository.existsByEmail(NAMI_EMAIL)).thenReturn(false);
        testee.createAccount(nami);
        verify(accountRepository).save(nami);
    }
}
