package drk.shopamos.rest.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import drk.shopamos.rest.model.entity.User;
import drk.shopamos.rest.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @InjectMocks UserService testee;
    private String email;
    private User user;

    @BeforeEach
    void setup() {
        email = "username@domain.com";
        user = new User();
        user.setEmail(email);
    }

    @Test
    @DisplayName("loadUserByUsername - finds the user by email from user repository")
    void loadUserByUsername_findsUserByEmail() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User foundUser = testee.loadUserByUsername(email);
        assertThat(user, is(foundUser));
    }

    @Test
    @DisplayName("loadUserByUsername - throws exception when it cannot find the user by email")
    void loadUserByUsername_throwsExceptionWhenNotFound() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> testee.loadUserByUsername(email));
    }
}
