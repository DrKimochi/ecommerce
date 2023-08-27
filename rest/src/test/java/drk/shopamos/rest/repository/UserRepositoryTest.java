package drk.shopamos.rest.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import drk.shopamos.rest.model.entity.User;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
class UserRepositoryTest {

    @Autowired private UserRepository testee;

    @Test
    @DisplayName("findByEmail - Finds user by email with fields populated as expected")
    void findByEmail_findsUser() {
        Optional<User> userOpt = testee.findByEmail("luffy@mugiwara.com");
        assertThat(userOpt.isPresent(), is(true));
        User user = userOpt.get();
        assertThat(user.getEmail(), is("luffy@mugiwara.com"));
        assertThat(user.getName(), is("Monkey D Luffy"));
        assertThat(user.getId(), notNullValue());
        assertThat(user.getUsername(), is("luffy@mugiwara.com"));
        assertThat(user.getPassword(), is("meat123"));
        assertThat(user.isAdmin(), is(true));
        assertThat(user.isActive(), is(true));
    }

    @Test
    @DisplayName("findByEmail - returns empty optional when user is not found")
    void findByEmail_cannotFindUser() {
        Optional<User> userOpt = testee.findByEmail("usopp@mugiwara.com");
        assertThat(userOpt.isEmpty(), is(true));
    }
}
