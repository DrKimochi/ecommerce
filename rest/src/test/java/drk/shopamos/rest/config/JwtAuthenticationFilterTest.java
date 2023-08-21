package drk.shopamos.rest.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import drk.shopamos.rest.model.entity.User;
import drk.shopamos.rest.service.UserService;

import jakarta.servlet.FilterChain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    public static final String CUSTOMER_AUTHORITY = "CUSTOMER";
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String BEARER_TOKEN = "Bearer aToken";
    private static final String USERNAME = "anUsername";
    private static final String TOKEN = "aToken";
    public static final String BASIC_TOKEN = "Basic abcdefg";
    @Captor ArgumentCaptor<UsernamePasswordAuthenticationToken> authToken;
    @Mock JwtTokenHelper jwtTokenHelper;
    @Mock UserService userService;
    @Mock private FilterChain filterChain;
    @Mock private SecurityContext securityContext;
    @InjectMocks private JwtAuthenticationFilter testee;

    @BeforeEach
    void setup() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName(
            "When Authorization header does not exist then SecurityContextHolder is not set but filter chain continues")
    void doFilterInternal_whenNoAuthorizationHeader_thenSecurityContextNotSet() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        testee.doFilterInternal(request, response, filterChain);

        verify(securityContext, times(0)).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName(
            "When Authorization header does not start with 'Bearer ' then SecurityContextHolder is not set but filter chain continues")
    void doFilterInternal_whenNotValidAuthorizationHeaderPrefix_thenSecurityContextNotSet()
            throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION_HEADER_NAME, BASIC_TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();

        testee.doFilterInternal(request, response, filterChain);

        verify(securityContext, times(0)).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName(
            "When SecurityContext already contains an authentication then SecurityContextHolder is not set but filter chain continues")
    void doFilterInternal_whenNotUsername_thenSecurityContextNotSet() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION_HEADER_NAME, BEARER_TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenHelper.extractUsername(TOKEN)).thenReturn(USERNAME);
        when(securityContext.getAuthentication()).thenReturn(mock(Authentication.class));

        testee.doFilterInternal(request, response, filterChain);

        verify(securityContext, times(0)).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName(
            "When token is invalid then SecurityContextHolder is not set but filter chain continues")
    void doFilterInternal_whenInvalidToken_thenSecurityContextNotSet() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION_HEADER_NAME, BEARER_TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();
        User user = new User();

        when(jwtTokenHelper.extractUsername(TOKEN)).thenReturn(USERNAME);
        when(userService.loadUserByUsername(USERNAME)).thenReturn(user);
        when(jwtTokenHelper.isTokenValid(TOKEN, user)).thenReturn(false);

        testee.doFilterInternal(request, response, filterChain);

        verify(securityContext, times(0)).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("When token is valid then SecurityContextHolder is set and filter chain continues")
    void doFilterInternal_whenValidToken_thenSecurityContextIsSet() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION_HEADER_NAME, BEARER_TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();
        User user = new User();
        List<SimpleGrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority(CUSTOMER_AUTHORITY));

        when(jwtTokenHelper.extractUsername(TOKEN)).thenReturn(USERNAME);
        when(userService.loadUserByUsername(USERNAME)).thenReturn(user);
        when(jwtTokenHelper.isTokenValid(TOKEN, user)).thenReturn(true);

        testee.doFilterInternal(request, response, filterChain);

        verify(securityContext, times(1)).setAuthentication(authToken.capture());
        verify(filterChain).doFilter(request, response);

        assertThat(authToken.getValue().getDetails(), is(notNullValue()));
        assertThat(authToken.getValue().getPrincipal(), is(user));
        assertThat(authToken.getValue().getCredentials(), is(nullValue()));
        assertThat(authToken.getValue().getAuthorities(), is(authorities));
    }
}
