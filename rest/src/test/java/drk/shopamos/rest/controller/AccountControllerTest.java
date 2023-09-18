package drk.shopamos.rest.controller;

import static drk.shopamos.rest.mother.AccountMother.NAMI_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.NAMI_NAME;
import static drk.shopamos.rest.mother.AccountMother.assertAccountRequestEqualsAccountEntity;
import static drk.shopamos.rest.mother.AccountMother.buildAccountNami;
import static drk.shopamos.rest.mother.AccountMother.buildAccountRequestNami;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import drk.shopamos.rest.argument.BadEmailArgumentProvider;
import drk.shopamos.rest.argument.BadPasswordArgumentProvider;
import drk.shopamos.rest.config.MessageProvider;
import drk.shopamos.rest.controller.advice.ControllerExceptionHandler;
import drk.shopamos.rest.controller.mapper.AccountMapperImpl;
import drk.shopamos.rest.controller.request.AccountRequest;
import drk.shopamos.rest.controller.response.ErrorResponse;
import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.service.AccountService;
import drk.shopamos.rest.service.exception.EntityNotFoundException;

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

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(
        classes = {
            AccountController.class,
            ControllerExceptionHandler.class,
            AccountMapperImpl.class,
            MessageProvider.class
        })
class AccountControllerTest extends ControllerTest {

    @Captor private ArgumentCaptor<Account> accountArgumentCaptor;

    @MockBean private AccountService accountService;

    @Test
    @DisplayName("createAccount - when body is missing then return 400 response with message")
    void createAccount_whenBodyMissing_thenReturn400ErrorResponse() throws Exception {
        ErrorResponse errorResponse = postMvcRequestExpectingStatus400("/v1/accounts", null);
        assertRequestBodyUnreadableError(errorResponse);
    }

    @Test
    @DisplayName("createAccount - when name is missing then return 400 response with message")
    void createAccount_whenNameMissing_thenReturn400ErrorResponse() throws Exception {
        AccountRequest request = AccountRequest.builder().build();
        ErrorResponse errorResponse = postMvcRequestExpectingStatus400("/v1/accounts", request);
        assertEmptyFieldValidation(errorResponse, "name");
    }

    @ParameterizedTest
    @ArgumentsSource(BadEmailArgumentProvider.class)
    @DisplayName("createAccount - when email has bad format then return 400 response with message")
    void createAccount_whenEmailBadFormat_thenReturn400ErrorResponse(String email)
            throws Exception {
        AccountRequest request = AccountRequest.builder().name(NAMI_NAME).email(email).build();
        ErrorResponse errorResponse = postMvcRequestExpectingStatus400("/v1/accounts", request);
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
                        .name(NAMI_NAME)
                        .email(NAMI_EMAIL)
                        .password(password)
                        .build();

        ErrorResponse errorResponse = postMvcRequestExpectingStatus400("/v1/accounts", request);
        assertPasswordValidation(errorResponse);
    }

    @Test
    @DisplayName(
            "createAccount - when required data provided then call service layer and return 200 response")
    void createAccount_wheRequiredDataProvided_thenReturn200Response() throws Exception {
        AccountRequest request = buildAccountRequestNami();

        postMvcRequestExpectingStatus200("/v1/accounts", request);

        verify(accountService).createAccount(accountArgumentCaptor.capture());
        assertAccountRequestEqualsAccountEntity(request, accountArgumentCaptor.getValue());
    }

    @Test
    @DisplayName(
            "createAccount - when service Throws EntityNotFoundException then return 400 Response with message")
    void createAccount_whenServiceThrowsEntityNotFoundException_thenReturn400WithMessage()
            throws Exception {
        Account account = buildAccountNami();
        AccountRequest request = buildAccountRequestNami();

        doThrow(new EntityNotFoundException(messageProvider, account.getEmail()))
                .when(accountService)
                .createAccount(account);

        ErrorResponse errorResponse = postMvcRequestExpectingStatus400("/v1/accounts", request);

        assertEntityNotFoundError(errorResponse, account.getEmail());
    }
}
