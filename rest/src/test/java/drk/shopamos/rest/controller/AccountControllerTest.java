package drk.shopamos.rest.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;

import drk.shopamos.rest.argument.BadEmailArgumentProvider;
import drk.shopamos.rest.argument.BadPasswordArgumentProvider;
import drk.shopamos.rest.controller.advice.ControllerExceptionHandler;
import drk.shopamos.rest.controller.mapper.AccountMapperImpl;
import drk.shopamos.rest.controller.request.AccountRequest;
import drk.shopamos.rest.controller.response.ErrorResponse;
import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.service.AccountService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(
        classes = {
            AccountController.class,
            ControllerExceptionHandler.class,
            AccountMapperImpl.class
        })
class AccountControllerTest extends ControllerTest {

    @Captor private ArgumentCaptor<Account> accountArgumentCaptor;

    @MockBean private AccountService accountService;

    @Test
    @DisplayName("createAccount - when body is missing then return 400 response with message")
    void createAccount_whenBodyMissing_thenReturn400ErrorResponse() throws Exception {
        MvcResult mvcResult = postMvcRequestExpectingStatus400("/v1/accounts", null);
        ErrorResponse errorResponse = readErrorResponse(mvcResult);
        assertRequestBodyUnreadableError(errorResponse);
    }

    @Test
    @DisplayName("createAccount - when name is missing then return 400 response with message")
    void createAccount_whenNameMissing_thenReturn400ErrorResponse() throws Exception {
        AccountRequest request = AccountRequest.builder().build();
        MvcResult mvcResult = postMvcRequestExpectingStatus400("/v1/accounts", request);
        ErrorResponse errorResponse = readErrorResponse(mvcResult);
        assertEmptyFieldValidation(errorResponse, "name");
    }

    @ParameterizedTest
    @ArgumentsSource(BadEmailArgumentProvider.class)
    @DisplayName("createAccount - when email has bad format then return 400 response with message")
    void createAccount_whenEmailBadFormat_thenReturn400ErrorResponse(String email)
            throws Exception {
        AccountRequest request = AccountRequest.builder().name(SOME_NAME).email(email).build();
        MvcResult mvcResult = postMvcRequestExpectingStatus400("/v1/accounts", request);
        ErrorResponse errorResponse = readErrorResponse(mvcResult);
        assertEmailValidation(errorResponse);
    }

    @ParameterizedTest
    @ArgumentsSource(BadPasswordArgumentProvider.class)
    @DisplayName(
            "createAccount - when password has bad format then return 400 response with message")
    void createAccount_whePasswordBadFormat_thenReturn400ErrorResponse(String password)
            throws Exception {
        AccountRequest request =
                AccountRequest.builder()
                        .name(SOME_NAME)
                        .email(SOME_EMAIL)
                        .password(password)
                        .build();

        MvcResult mvcResult = postMvcRequestExpectingStatus400("/v1/accounts", request);
        ErrorResponse errorResponse = readErrorResponse(mvcResult);
        assertPasswordValidation(errorResponse);
    }

    @Test
    @DisplayName(
            "createAccount - when required data provided then call service layer and return 200 response")
    void createAccount_wheRequiredDataProvided_thenReturn200Response() throws Exception {
        AccountRequest request =
                AccountRequest.builder()
                        .name(SOME_NAME)
                        .email(SOME_EMAIL)
                        .password(SOME_PASSWORD)
                        .isActive(true)
                        .isAdmin(false)
                        .build();

        postMvcRequestExpectingStatus200("/v1/accounts", request);

        verify(accountService).createAccount(accountArgumentCaptor.capture());
        assertAccountEntityMapped(request, accountArgumentCaptor.getValue());
    }

    void assertAccountEntityMapped(AccountRequest accountRequest, Account account) {
        assertThat(accountRequest.getName(), is(account.getName()));
        assertThat(accountRequest.getEmail(), is(account.getEmail()));
        assertThat(accountRequest.getPassword(), is(account.getPassword()));
        assertThat(accountRequest.isActive(), is(account.isActive()));
        assertThat(accountRequest.isAdmin(), is(account.isAdmin()));
    }
}
