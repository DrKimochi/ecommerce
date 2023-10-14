package drk.shopamos.rest.service;

import static drk.shopamos.rest.config.MessageProvider.MSG_NOT_FOUND_USER;
import static drk.shopamos.rest.mother.AccountMother.LUFFY_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.LUFFY_ENCODED_PWD;
import static drk.shopamos.rest.mother.AccountMother.LUFFY_ID;
import static drk.shopamos.rest.mother.AccountMother.VIVI_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.buildAdminLuffy;
import static drk.shopamos.rest.mother.AccountMother.buildAdminLuffyWithEncodedPwd;
import static drk.shopamos.rest.mother.MessageMother.MSG_EXISTS_EMAIL;
import static drk.shopamos.rest.mother.MessageMother.MSG_NOT_FOUND_ID;

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
import drk.shopamos.rest.service.exception.EntityNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest extends ServiceTest {
    @Mock AccountRepository accountRepository;
    @Mock MessageProvider messageProvider;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks AccountService testee;

    @Test
    @DisplayName("loadUserByUsername - finds the account by email from account repository")
    void loadUserByUsername_findsUserByEmail() {
        Account luffy = buildAdminLuffy();
        when(accountRepository.findByEmail(LUFFY_EMAIL)).thenReturn(Optional.of(luffy));
        Account foundAccount = testee.loadUserByUsername(LUFFY_EMAIL);
        assertThat(luffy, is(foundAccount));
    }

    @Test
    @DisplayName("loadUserByUsername - throws exception when it cannot find the account by email")
    void loadUserByUsername_throwsExceptionWhenNotFound() {
        when(accountRepository.findByEmail(VIVI_EMAIL)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> testee.loadUserByUsername(VIVI_EMAIL));
        verify(messageProvider).getMessage(MSG_NOT_FOUND_USER, VIVI_EMAIL);
    }

    @Test
    @DisplayName("createAccount - throws exception when account already exists")
    void createAccount_throwsExceptionWhenAccountAlreadyExists() {
        Account luffy = buildAdminLuffy();
        when(accountRepository.existsByEmail(LUFFY_EMAIL)).thenReturn(true);
        assertThrows(EntityExistsException.class, () -> testee.createAccount(luffy));
        verify(messageProvider).getMessage(MSG_EXISTS_EMAIL, LUFFY_EMAIL);
        verify(accountRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("createAccount - saves account when it doesnt exist yet")
    void createAccount_savesWhenAccountDoesntExistYet() {
        Account luffy = buildAdminLuffy();
        when(accountRepository.existsByEmail(LUFFY_EMAIL)).thenReturn(false);
        testee.createAccount(luffy);
        verify(accountRepository).save(luffy);
    }

    @Test
    @DisplayName("updateAccount -throws exception when ID does not exist")
    void updateAccount_throwsExceptionWhenIdDoesNotExist() {
        Account luffy = buildAdminLuffy();
        when(accountRepository.existsById(LUFFY_ID)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> testee.updateAccount(luffy));
        verify(messageProvider).getMessage(MSG_NOT_FOUND_ID, LUFFY_ID);
        verify(accountRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("updateAccount - save account with encoded password when validations pass")
    void updateAccount_savesWhenValidationsPass() {
        Account luffy = buildAdminLuffy();
        when(accountRepository.existsById(LUFFY_ID)).thenReturn(true);
        when(passwordEncoder.encode(luffy.getPassword())).thenReturn(LUFFY_ENCODED_PWD);
        testee.updateAccount(luffy);
        verify(accountRepository).save(buildAdminLuffyWithEncodedPwd());
    }

    @Test
    @DisplayName("deleteAccount - throws exception when ID does not exist")
    void deleteAccount_throwsExceptionWhenIdDoesNotExist() {
        when(accountRepository.existsById(LUFFY_ID)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> testee.deleteAccount(LUFFY_ID));
        verify(messageProvider).getMessage(MSG_NOT_FOUND_ID, LUFFY_ID);
        verify(accountRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("deleteAccount - deletes by ID when the ID exists")
    void deleteAccount_deletesById_whenIdExists() {
        when(accountRepository.existsById(LUFFY_ID)).thenReturn(true);
        testee.deleteAccount(LUFFY_ID);
        verify(accountRepository).deleteById(LUFFY_ID);
    }
}
