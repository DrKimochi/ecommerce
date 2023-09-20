package drk.shopamos.rest.service;

import static drk.shopamos.rest.mother.AccountMother.LUFFY_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.LUFFY_ID;
import static drk.shopamos.rest.mother.AccountMother.VIVI_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.buildAdminLuffy;

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
import drk.shopamos.rest.service.exception.IllegalDataException;

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
        verify(messageProvider).getMessage(MSG_EMAIL_EXISTS, LUFFY_EMAIL);
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
    @DisplayName("updateAccount -throws exception when user is deactivating himself")
    void updateAccount_throwsExceptionWhenUserDeactivatingHimself() {
        Account luffy = buildAdminLuffy();
        luffy.setActive(false);
        mockPrincipalAccount(luffy);
        when(accountRepository.existsById(LUFFY_ID)).thenReturn(true);
        assertThrows(IllegalDataException.class, () -> testee.updateAccount(luffy));
        verify(messageProvider).getMessage(MSG_CANNOT_DEACTIVATE_ACCOUNT);
        verify(accountRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("updateAccount -throws exception when user is demoting himself")
    void updateAccount_throwsExceptionWhenUserDemotingHimself() {
        Account luffy = buildAdminLuffy();
        luffy.setAdmin(false);
        mockPrincipalAccount(luffy);
        when(accountRepository.existsById(LUFFY_ID)).thenReturn(true);
        assertThrows(IllegalDataException.class, () -> testee.updateAccount(luffy));
        verify(messageProvider).getMessage(MessageProvider.MSG_CANNOT_DEMOTE);
        verify(accountRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("updateAccount - save account when validations pass")
    void updateAccount_savesWhenValidationsPass() {
        Account luffy = buildAdminLuffy();
        when(accountRepository.existsById(LUFFY_ID)).thenReturn(true);
        testee.updateAccount(luffy);
        verify(accountRepository).save(luffy);
    }
}
