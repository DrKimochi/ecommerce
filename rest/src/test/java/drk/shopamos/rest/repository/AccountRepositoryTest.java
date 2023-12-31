package drk.shopamos.rest.repository;

import static drk.shopamos.rest.mother.AccountMother.LUFFY_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.LUFFY_ID;
import static drk.shopamos.rest.mother.AccountMother.LUFFY_NAME;
import static drk.shopamos.rest.mother.AccountMother.NAMI_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.NAMI_NAME;
import static drk.shopamos.rest.mother.AccountMother.NAMI_PWD;
import static drk.shopamos.rest.mother.AccountMother.VIVI_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.VIVI_ID;
import static drk.shopamos.rest.mother.AccountMother.ZORO_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.buildAdminLuffy;
import static drk.shopamos.rest.mother.AccountMother.buildCustomerNamiWithoutId;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import drk.shopamos.rest.model.entity.Account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired private AccountRepository testee;

    @Test
    @DisplayName("findByEmail - Finds account by email with fields populated as expected")
    void findByEmail_whenAccountExists_thenFindsAccount() {
        Optional<Account> accountOpt = testee.findByEmail(LUFFY_EMAIL);
        assertThat(accountOpt.isPresent(), is(true));
        assertThat(accountOpt.get(), is(buildAdminLuffy()));
    }

    @Test
    @DisplayName("findByEmail - returns empty optional when account is not found")
    void findByEmail_whenAccountDoesntExist_returnsEmptyOptional() {
        Optional<Account> accountOpt = testee.findByEmail(VIVI_EMAIL);
        assertThat(accountOpt.isEmpty(), is(true));
    }

    @Test
    @DisplayName("existsById - returns true when id exists")
    void existsById_whenIdExists_returnsTrue() {
        assertThat(testee.existsById(1), is(true));
    }

    @Test
    @DisplayName("existsById - returns false when id does not exist")
    void existsById_whenIdDoesNotExist_returnsFalse() {
        assertThat(testee.existsById(100), is(false));
    }

    @Test
    @DisplayName("save - when account passes validations, then it is saved to database ")
    void save_whenValidAccount_thenSavesToDb() {
        testee.saveAndFlush(buildCustomerNamiWithoutId());
        Optional<Account> expectedSavedAccountOpt = testee.findByEmail(NAMI_EMAIL);
        assertThat(expectedSavedAccountOpt.isPresent(), is(true));
        assertAccountNamiWithId(expectedSavedAccountOpt.get());
    }

    @Test
    @DisplayName("save - when email already exists then throw error ")
    void save_whenEmailExists_thenThrowError() {
        Account account = buildCustomerNamiWithoutId();
        account.setEmail(ZORO_EMAIL);
        assertThrows(DataIntegrityViolationException.class, () -> testee.saveAndFlush(account));
    }

    @Test
    @DisplayName("save - when email missing then throw error")
    void save_whenEmailMissing_thenThrowError() {
        Account account = buildCustomerNamiWithoutId();
        account.setEmail(null);
        assertThrows(DataIntegrityViolationException.class, () -> testee.saveAndFlush(account));
    }

    @Test
    @DisplayName("save - when password missing then throw error")
    void save_whenPasswordMissing_thenThrowError() {
        Account account = buildCustomerNamiWithoutId();
        account.setPassword(null);
        assertThrows(DataIntegrityViolationException.class, () -> testee.saveAndFlush(account));
    }

    @Test
    @DisplayName("deleteById - when ID exists then row is deleted")
    void deleteById_whenIdExists_thenDeleteRow() {
        Integer accountId = testee.saveAndFlush(buildCustomerNamiWithoutId()).getId();
        assertThat(testee.existsById(accountId), is(true));
        testee.deleteById(accountId);
        assertThat(testee.existsById(accountId), is(false));
    }

    @Test
    @DisplayName("findById -  Finds account by Id with fields populated as expected")
    void findById_whenIdExists_thenReturnAccount() {
        Optional<Account> accountOpt = testee.findById(LUFFY_ID);
        assertThat(accountOpt.isPresent(), is(true));
        assertThat(accountOpt.get(), is(buildAdminLuffy()));
    }

    @Test
    @DisplayName("findById - returns empty optional when account is not found")
    void findById_whenAccountDoesntExist_returnsEmptyOptional() {
        Optional<Account> accountOpt = testee.findById(VIVI_ID);
        assertThat(accountOpt.isEmpty(), is(true));
    }

    @Test
    @DisplayName("findAllByAttributes - finds by name contains and case insensitive")
    void findAllByAttributes_findsByName() {
        List<Account> foundAccounts = testee.findAllByAttributes("uFf", null, null, null);
        assertThat(foundAccounts.size(), is(1));
        assertThat(foundAccounts.get(0).getName(), is(LUFFY_NAME));
    }

    @Test
    @DisplayName("findAllByAttributes - finds by email contains and case insensitive")
    void findAllByAttributes_findsByEmail() {
        List<Account> foundAccounts = testee.findAllByAttributes(null, "uFf", null, null);
        assertThat(foundAccounts.size(), is(1));
        assertThat(foundAccounts.get(0).getName(), is(LUFFY_NAME));
    }

    @Test
    @DisplayName("findAllByAttributes - finds by isAdmin")
    void findAllByAttributes_findsByIsAdmin() {
        List<Account> foundAccounts = testee.findAllByAttributes(null, null, false, null);
        assertThat(foundAccounts.size(), is(1));
        assertThat(foundAccounts.get(0).getEmail(), is(ZORO_EMAIL));
    }

    @Test
    @DisplayName("findAllByAttributes - finds by isActive")
    void findAllByAttributes_findsByIsActive() {
        List<Account> foundAccounts = testee.findAllByAttributes(null, null, null, true);
        assertThat(foundAccounts.size(), is(1));
        assertThat(foundAccounts.get(0).getName(), is(LUFFY_NAME));
    }

    @Test
    @DisplayName("findAllByAttributes - finds by all attributes")
    void findAllByAttributes_findsByAll() {
        List<Account> foundAccounts = testee.findAllByAttributes("zOr", "uGiWara", false, false);
        assertThat(foundAccounts.size(), is(1));
        assertThat(foundAccounts.get(0).getEmail(), is(ZORO_EMAIL));
    }

    @Test
    @DisplayName("findAllByAttributes - returns all accounts when no attribute passed")
    void findAllByAttributes_returnsAllAccounts() {
        List<Account> foundAccounts = testee.findAllByAttributes(null, null, null, null);
        assertThat(foundAccounts.size(), is(2));
    }

    private void assertAccountNamiWithId(Account account) {
        assertThat(account.getId(), is(notNullValue()));
        assertThat(account.getEmail(), is(NAMI_EMAIL));
        assertThat(account.getUsername(), is(NAMI_EMAIL));
        assertThat(account.getPassword(), is(NAMI_PWD));
        assertThat(account.getName(), is(NAMI_NAME));
        assertThat(account.isActive(), is(true));
        assertThat(account.isAdmin(), is(false));
    }
}
