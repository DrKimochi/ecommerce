package drk.shopamos.rest.controller;

import static drk.shopamos.rest.mother.AccountMother.LUFFY_EMAIL;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;

import drk.shopamos.rest.config.JwtAuthenticationFilter;
import drk.shopamos.rest.config.JwtTokenHelper;
import drk.shopamos.rest.config.MessageProvider;
import drk.shopamos.rest.config.SecurityConfiguration;
import drk.shopamos.rest.config.ShopamosConfiguration;
import drk.shopamos.rest.controller.advice.ControllerExceptionHandler;
import drk.shopamos.rest.controller.assertion.ErrorResponseAssert;
import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.service.AccountService;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@ContextConfiguration(
        classes = {
            ControllerExceptionHandler.class,
            MessageProvider.class,
            ShopamosConfiguration.class,
            SecurityConfiguration.class,
            JwtAuthenticationFilter.class,
            JwtTokenHelper.class,
            ErrorResponseAssert.class
        })
public abstract class ControllerTest {

    protected static String SOME_TOKEN = "xxxxx.yyyyy.zzzzz";

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired protected MessageProvider messageProvider;
    @Autowired protected JwtTokenHelper jwtTokenHelper;
    @Autowired protected ErrorResponseAssert errorResponseAssert;
    @MockBean protected AccountService accountService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    protected void setup() {
        SecurityContextHolder.clearContext();
    }

    protected MockMvcHandler getMvc() {
        return new MockMvcHandler(mockMvc, objectMapper);
    }

    protected String adminToken(Integer accountId) {
        return getJwtToken(accountId, true);
    }

    protected String customerToken(Integer accountId) {
        return getJwtToken(accountId, false);
    }

    protected String token(Integer accountId, boolean isAdmin) {
        return getJwtToken(accountId, isAdmin);
    }

    private String getJwtToken(Integer accountId, boolean isAdmin) {
        Account account = new Account();
        account.setId(accountId);
        account.setEmail(LUFFY_EMAIL);
        account.setAdmin(isAdmin);
        when(accountService.loadUserByUsername(LUFFY_EMAIL)).thenReturn(account);
        return jwtTokenHelper.generateToken(account);
    }
}
