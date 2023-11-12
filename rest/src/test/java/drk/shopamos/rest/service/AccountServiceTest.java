package drk.shopamos.rest.service;

import static drk.shopamos.rest.config.MessageProvider.MSG_NOT_FOUND_USER;
import static drk.shopamos.rest.mother.AccountMother.LUFFY_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.LUFFY_ENCODED_PWD;
import static drk.shopamos.rest.mother.AccountMother.LUFFY_ID;
import static drk.shopamos.rest.mother.AccountMother.LUFFY_NAME;
import static drk.shopamos.rest.mother.AccountMother.VIVI_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.buildAdminLuffy;
import static drk.shopamos.rest.mother.AccountMother.buildAdminLuffyWithEncodedPwd;
import static drk.shopamos.rest.mother.MessageMother.MSG_EXISTS_EMAIL;
import static drk.shopamos.rest.mother.MessageMother.MSG_NOT_FOUND_ID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    @Mock AccountRepository repository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks AccountService testee;

    @Test
    @DisplayName("loadUserByUsername - finds the account by email from account repository")
    void loadUserByUsername_findsUserByEmail() {
        Account luffy = buildAdminLuffy();
        when(repository.findByEmail(LUFFY_EMAIL)).thenReturn(Optional.of(luffy));
        Account foundAccount = testee.loadUserByUsername(LUFFY_EMAIL);
        assertThat(luffy, is(foundAccount));
    }

    @Test
    @DisplayName("loadUserByUsername - throws exception when it cannot find the account by email")
    void loadUserByUsername_throwsExceptionWhenNotFound() {
        when(repository.findByEmail(VIVI_EMAIL)).thenReturn(Optional.empty());
        assertException(
                UsernameNotFoundException.class,
                () -> testee.loadUserByUsername(VIVI_EMAIL),
                MSG_NOT_FOUND_USER,
                VIVI_EMAIL);
    }

    @Test
    @DisplayName("createAccount - throws exception when account already exists")
    void createAccount_throwsExceptionWhenAccountAlreadyExists() {
        Account luffy = buildAdminLuffy();
        when(repository.findByEmail(LUFFY_EMAIL)).thenReturn(Optional.of(new Account()));
        assertException(
                EntityExistsException.class,
                () -> testee.createAccount(luffy),
                MSG_EXISTS_EMAIL,
                LUFFY_EMAIL);
        verify(repository, times(0)).save(any());
    }

    @Test
    @DisplayName("createAccount - saves account when it doesnt exist yet")
    void createAccount_savesWhenAccountDoesntExistYet() {
        Account luffy = buildAdminLuffy();
        when(repository.findByEmail(LUFFY_EMAIL)).thenReturn(Optional.empty());
        testee.createAccount(luffy);
        verify(repository).save(luffy);
    }

    @Test
    @DisplayName("updateAccount -throws exception when ID does not exist")
    void updateAccount_throwsExceptionWhenIdDoesNotExist() {
        Account luffy = buildAdminLuffy();
        when(repository.findById(LUFFY_ID)).thenReturn(Optional.empty());
        assertException(
                EntityNotFoundException.class,
                () -> testee.updateAccount(luffy),
                MSG_NOT_FOUND_ID,
                LUFFY_ID);
        verify(repository, times(0)).save(any());
    }

    @Test
    @DisplayName("updateAccount - save account with encoded password when validations pass")
    void updateAccount_savesWhenValidationsPass() {
        Account luffy = buildAdminLuffy();
        when(repository.findById(LUFFY_ID)).thenReturn(Optional.of(new Account()));
        when(passwordEncoder.encode(luffy.getPassword())).thenReturn(LUFFY_ENCODED_PWD);
        testee.updateAccount(luffy);
        verify(repository).save(buildAdminLuffyWithEncodedPwd());
    }

    @Test
    @DisplayName("deleteAccount - throws exception when ID does not exist")
    void deleteAccount_throwsExceptionWhenIdDoesNotExist() {
        when(repository.findById(LUFFY_ID)).thenReturn(Optional.empty());
        assertException(
                EntityNotFoundException.class,
                () -> testee.deleteAccount(LUFFY_ID),
                MSG_NOT_FOUND_ID,
                LUFFY_ID);
        verify(repository, times(0)).delete(any());
    }

    @Test
    @DisplayName("deleteAccount - deletes by ID when the ID exists")
    void deleteAccount_deletesById_whenIdExists() {
        Account luffy = buildAdminLuffy();
        when(repository.findById(LUFFY_ID)).thenReturn(Optional.of(luffy));
        testee.deleteAccount(LUFFY_ID);
        verify(repository).delete(luffy);
    }

    @Test
    @DisplayName("getAccount - returns account when it is found")
    void getAccount_returnsAccount_whenItIsFound() {
        Account luffy = buildAdminLuffy();
        when(repository.findById(LUFFY_ID)).thenReturn(Optional.of(luffy));
        Account returnedAccount = testee.getAccount(LUFFY_ID);
        assertThat(luffy, is(returnedAccount));
    }

    @Test
    @DisplayName("getAccount - throws exception when it is not found")
    void getAccount_throwsException_whenNotFound() {
        when(repository.findById(LUFFY_ID)).thenReturn(Optional.empty());
        assertException(
                EntityNotFoundException.class,
                () -> testee.getAccount(LUFFY_ID),
                MSG_NOT_FOUND_ID,
                LUFFY_ID);
    }

    @Test
    @DisplayName("getAccounts - invokes repository findAllByAttributes passing down the parameters")
    void getAccounts_invokes_findAllByAttributes() {
        testee.getAccounts(LUFFY_NAME, LUFFY_EMAIL, true, false);
        verify(repository).findAllByAttributes(LUFFY_NAME, LUFFY_EMAIL, true, false);
    }
}
