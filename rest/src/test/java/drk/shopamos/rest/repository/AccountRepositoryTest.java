package drk.shopamos.rest.repository;

import static drk.shopamos.rest.mother.AccountMother.LUFFY_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.NAMI_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.VIVI_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.ZORO_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.assertAccountDataLuffy;
import static drk.shopamos.rest.mother.AccountMother.assertAccountDataNami;
import static drk.shopamos.rest.mother.AccountMother.buildAccountNami;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import drk.shopamos.rest.model.entity.Account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired private AccountRepository testee;

    @Test
    @DisplayName("findByEmail - Finds account by email with fields populated as expected")
    void findByEmail_whenAccountExists_thenFindsAccount() {
        Optional<Account> accountOpt = testee.findByEmail(LUFFY_EMAIL);
        assertThat(accountOpt.isPresent(), is(true));
        assertAccountDataLuffy(accountOpt.get());
    }

    @Test
    @DisplayName("findByEmail - returns empty optional when account is not found")
    void findByEmail_whenAccountDoesntExist_returnsEmptyOptional() {
        Optional<Account> accountOpt = testee.findByEmail(VIVI_EMAIL);
        assertThat(accountOpt.isEmpty(), is(true));
    }
    @Test
    @DisplayName("existsByEmail - returns true when email exists")
    void existsByEmail_whenEmailExists_returnsTrue() {
        assertThat(testee.existsByEmail(LUFFY_EMAIL), is(true));
    }

    @Test
    @DisplayName("existsByEmail - returns false when email does not exist")
    void existsByEmail_whenEmailDoesNotExist_returnsFalse() {
        assertThat(testee.existsByEmail(VIVI_EMAIL), is(false));
    }

    @Test
    @DisplayName("createAccount - when object has required data, then it is saved to database ")
    void save_whenValidParameter_thenSavesToDb() {
        testee.save(buildAccountNami());
        Optional<Account> expectedSavedAccountOpt = testee.findByEmail(NAMI_EMAIL);
        assertThat(expectedSavedAccountOpt.isPresent(), is(true));
        assertAccountDataNami(expectedSavedAccountOpt.get());
    }

    @Test
    @DisplayName("save - when email already exists then throw error ")
    void save_whenEmailExists_thenThrowError() {
        Account account = buildAccountNami();
        account.setEmail(ZORO_EMAIL);
        assertThrows(DataIntegrityViolationException.class, () -> testee.save(account));
    }

    @Test
    @DisplayName("save - when email missing then throw error")
    void save_whenEmailMissing_thenThrowError() {
        Account account = buildAccountNami();
        account.setEmail(null);
        assertThrows(DataIntegrityViolationException.class, () -> testee.save(account));
    }
    
    @Test
    @DisplayName("save - when password missing then throw error")
    void save_whenPasswordMissing_thenThrowError() {
        Account account = buildAccountNami();
        account.setPassword(null);
        assertThrows(DataIntegrityViolationException.class, () -> testee.save(account));
    }
    
    
}
