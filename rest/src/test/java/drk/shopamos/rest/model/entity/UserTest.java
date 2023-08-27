package drk.shopamos.rest.model.entity;

import static drk.shopamos.rest.model.enumerable.Role.ADMIN;
import static drk.shopamos.rest.model.enumerable.Role.CUSTOMER;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

class UserTest {

    @Test
    @DisplayName("getAuthorities - when user is an admin then roles are ADMIN and CUSTOMER")
    void getAuthorities_returnsCorrectRolesForAdmin() {
        User user = new User();
        user.setAdmin(true);
        assertThat(
                user.getAuthorities(),
                is(
                        List.of(
                                new SimpleGrantedAuthority(ADMIN.name()),
                                new SimpleGrantedAuthority(CUSTOMER.name()))));
    }

    @Test
    @DisplayName("getAuthorities - when user is NOT an admin then roles are CUSTOMER")
    void getAuthorities_returnsCorrectRolesForNonAdmin() {
        User user = new User();
        user.setAdmin(false);
        assertThat(user.getAuthorities(), is(List.of(new SimpleGrantedAuthority(CUSTOMER.name()))));
    }

    @Test
    @DisplayName("isAccountNonExpired - returns true")
    void isAccountNonExpired_returnsTrue() {
        User user = new User();
        assertThat(user.isAccountNonExpired(), is(true));
    }

    @Test
    @DisplayName("isAccountNonLocked - returns true")
    void isAccountNonLocked_returnsTrue() {
        User user = new User();
        assertThat(user.isAccountNonLocked(), is(true));
    }

    @Test
    @DisplayName("isCredentialsNonExpired - returns true")
    void isCredentialsNonExpired_returnsTrue() {
        User user = new User();
        assertThat(user.isAccountNonExpired(), is(true));
    }
}
