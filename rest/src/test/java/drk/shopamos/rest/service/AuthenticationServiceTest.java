package drk.shopamos.rest.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import drk.shopamos.rest.config.JwtTokenHelper;
import drk.shopamos.rest.model.entity.User;
import drk.shopamos.rest.repository.UserRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.NoSuchElementException;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    private final String username = "ausername@adomain.com";
    private final String password = "apassword123";
    private final User user = new User();
    private final UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(username, password);

    @Mock private AuthenticationManager authManager;
    @Mock private UserRepository userRepository;
    @Mock private JwtTokenHelper jwtTokenHelper;
    @InjectMocks private AuthenticationService testee;

    @Test
    @DisplayName("login - when authentication is successful then a jwt token string is returned")
    void login_returnsJwtToken() {
        final String jwtToken = "atoken";
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
        when(jwtTokenHelper.generateToken(user)).thenReturn(jwtToken);

        String expectedJwtToken = testee.login(username, password);

        verify(authManager, times(1)).authenticate(authToken);
        assertThat(jwtToken, is(expectedJwtToken));
    }

    @Test
    @DisplayName("login - when username cannot be found by email then exception is thrown")
    void login_throwsExceptionWhenUserNotFound() {
        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> testee.login(username, password));
    }
}
