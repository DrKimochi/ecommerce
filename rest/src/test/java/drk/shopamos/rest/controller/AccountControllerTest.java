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
import drk.shopamos.rest.controller.mapper.AccountMapperImpl;
import drk.shopamos.rest.controller.request.AccountRequest;
import drk.shopamos.rest.controller.response.ErrorResponse;
import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.service.exception.EntityNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;

@WebMvcTest
@ContextConfiguration(classes = {AccountController.class, AccountMapperImpl.class})
final class AccountControllerTest extends ControllerTest {

    @Captor private ArgumentCaptor<Account> accountArgumentCaptor;

    @Test
    @DisplayName("createAccount - when body is missing then return 400 response with message")
    void createAccount_whenBodyMissing_thenReturn400ErrorResponse() throws Exception {
        ErrorResponse errorResponse =
                sendPostRequestAssertingStatus400("/v1/accounts", withAdminToken(), null);
        assertRequestBodyUnreadableError(errorResponse);
    }

    @Test
    @DisplayName("createAccount - when name is missing then return 400 response with message")
    void createAccount_whenNameMissing_thenReturn400ErrorResponse() throws Exception {
        AccountRequest requestBody = AccountRequest.builder().build();
        ErrorResponse errorResponse =
                sendPostRequestAssertingStatus400("/v1/accounts", withAdminToken(), requestBody);
        assertEmptyFieldValidation(errorResponse, "name");
    }

    @ParameterizedTest
    @ArgumentsSource(BadEmailArgumentProvider.class)
    @DisplayName("createAccount - when email has bad format then return 400 response with message")
    void createAccount_whenEmailBadFormat_thenReturn400ErrorResponse(String email)
            throws Exception {
        AccountRequest requestBody = AccountRequest.builder().name(NAMI_NAME).email(email).build();
        ErrorResponse errorResponse =
                sendPostRequestAssertingStatus400("/v1/accounts", withAdminToken(), requestBody);
        assertEmailValidation(errorResponse);
    }

    @ParameterizedTest
    @ArgumentsSource(BadPasswordArgumentProvider.class)
    @DisplayName(
            "createAccount - when password has bad format then return 400 response with message")
    void createAccount_whenPasswordBadFormat_thenReturn400ErrorResponse(String password)
            throws Exception {
        AccountRequest requestBody =
                AccountRequest.builder()
                        .name(NAMI_NAME)
                        .email(NAMI_EMAIL)
                        .password(password)
                        .build();

        ErrorResponse errorResponse =
                sendPostRequestAssertingStatus400("/v1/accounts", withAdminToken(), requestBody);
        assertPasswordValidation(errorResponse);
    }

    @Test
    @DisplayName(
            "createAccount - when required data provided and user role is ADMIN then call service layer and return 200 response")
    void createAccount_whenRequiredDataProvided_thenReturn200Response() throws Exception {
        AccountRequest requestBody = buildAccountRequestNami();
        sendPostRequestAssertingStatus200("/v1/accounts", withAdminToken(), requestBody);
        verify(accountService).createAccount(accountArgumentCaptor.capture());
        assertAccountRequestEqualsAccountEntity(requestBody, accountArgumentCaptor.getValue());
    }

    @Test
    @DisplayName(
            "createAccount - when required data provided but user role is CUSTOMER then return 403 response")
    void createAccount_whenUserRoleIsCustomer_thenReturn403Response() throws Exception {
        AccountRequest requestBody = buildAccountRequestNami();
        sendPostRequestAssertingStatus403("/v1/accounts", withCustomerToken(), requestBody);
    }

    @Test
    @DisplayName("createAccount - when not authenticated then return 401 response")
    void createAccount_whenNotAuthenticated_thenReturn403Response() throws Exception {
        AccountRequest requestBody = buildAccountRequestNami();
        sendPostRequestAssertingStatus403("/v1/accounts", null, requestBody);
    }

    @Test
    @DisplayName(
            "createAccount - when service Throws EntityNotFoundException then return 400 Response with message")
    void createAccount_whenServiceThrowsEntityNotFoundException_thenReturn400WithMessage()
            throws Exception {
        Account account = buildAccountNami();
        AccountRequest requestBody = buildAccountRequestNami();

        doThrow(new EntityNotFoundException(messageProvider, account.getEmail()))
                .when(accountService)
                .createAccount(account);

        ErrorResponse errorResponse =
                sendPostRequestAssertingStatus400("/v1/accounts", withAdminToken(), requestBody);

        assertEntityNotFoundError(errorResponse, account.getEmail());
    }
}
