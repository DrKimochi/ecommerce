package drk.shopamos.rest.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import drk.shopamos.rest.model.entity.Account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired private AccountRepository testee;

    @Test
    @DisplayName("findByEmail - Finds user by email with fields populated as expected")
    void findByEmail_findsUser() {
        Optional<Account> accountOpt = testee.findByEmail("luffy@mugiwara.com");
        assertThat(accountOpt.isPresent(), is(true));
        Account account = accountOpt.get();
        assertThat(account.getEmail(), is("luffy@mugiwara.com"));
        assertThat(account.getName(), is("Monkey D Luffy"));
        assertThat(account.getId(), notNullValue());
        assertThat(account.getUsername(), is("luffy@mugiwara.com"));
        assertThat(account.getPassword(), is("meat123"));
        assertThat(account.isAdmin(), is(true));
        assertThat(account.isActive(), is(true));
    }

    @Test
    @DisplayName("findByEmail - returns empty optional when user is not found")
    void findByEmail_cannotFindUser() {
        Optional<Account> accountOpt = testee.findByEmail("usopp@mugiwara.com");
        assertThat(accountOpt.isEmpty(), is(true));
    }
}
