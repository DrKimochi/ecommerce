package drk.shopamos.rest.model.entity;

import static drk.shopamos.rest.model.enumerable.Role.ADMIN;
import static drk.shopamos.rest.model.enumerable.Role.CUSTOMER;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

class AccountTest {

    @Test
    @DisplayName("getAuthorities - when user is an admin then roles are ADMIN and CUSTOMER")
    void getAuthorities_returnsCorrectRolesForAdmin() {
        Account account = new Account();
        account.setAdmin(true);
        assertThat(
                account.getAuthorities(),
                is(
                        List.of(
                                new SimpleGrantedAuthority(ADMIN.name()),
                                new SimpleGrantedAuthority(CUSTOMER.name()))));
    }

    @Test
    @DisplayName("getAuthorities - when user is NOT an admin then roles are CUSTOMER")
    void getAuthorities_returnsCorrectRolesForNonAdmin() {
        Account account = new Account();
        account.setAdmin(false);
        assertThat(
                account.getAuthorities(), is(List.of(new SimpleGrantedAuthority(CUSTOMER.name()))));
    }

    @Test
    @DisplayName("isAccountNonExpired - returns true")
    void isAccountNonExpired_returnsTrue() {
        Account account = new Account();
        assertThat(account.isAccountNonExpired(), is(true));
    }

    @Test
    @DisplayName("isAccountNonLocked - returns true")
    void isAccountNonLocked_returnsTrue() {
        Account account = new Account();
        assertThat(account.isAccountNonLocked(), is(true));
    }

    @Test
    @DisplayName("isCredentialsNonExpired - returns true")
    void isCredentialsNonExpired_returnsTrue() {
        Account account = new Account();
        assertThat(account.isCredentialsNonExpired(), is(true));
    }

    @Test
    @DisplayName("isEnabled - returns isActive")
    void isEnabled_returnsIsActive() {
        Account account = new Account();
        account.setActive(true);
        assertThat(account.isEnabled(), is(true));
        account.setActive(false);
        assertThat(account.isEnabled(), is(false));
    }
}
