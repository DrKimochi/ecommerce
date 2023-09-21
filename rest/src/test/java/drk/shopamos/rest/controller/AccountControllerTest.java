package drk.shopamos.rest.controller;

import static drk.shopamos.rest.mother.AccountMother.NAMI_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.NAMI_NAME;
import static drk.shopamos.rest.mother.AccountMother.NAMI_PWD;
import static drk.shopamos.rest.mother.AccountMother.assertAccountResponseNami;
import static drk.shopamos.rest.mother.AccountMother.buildCustomerNami;
import static drk.shopamos.rest.mother.AccountMother.buildCustomerRequestNami;
import static drk.shopamos.rest.mother.AccountMother.buildNewCustomerNami;

import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

import drk.shopamos.rest.argument.BadEmailArgumentProvider;
import drk.shopamos.rest.argument.BadPasswordArgumentProvider;
import drk.shopamos.rest.controller.mapper.AccountMapperImpl;
import drk.shopamos.rest.controller.request.AccountRequest;
import drk.shopamos.rest.controller.response.AccountResponse;
import drk.shopamos.rest.controller.response.ErrorResponse;
import drk.shopamos.rest.model.entity.Account;

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

    public static final String CREATE_URI = "/v1/accounts";
    @Captor private ArgumentCaptor<Account> accountArgumentCaptor;

    @Test
    @DisplayName("createAccount - when body is missing then return 400 response with message")
    void createAccount_whenBodyMissing_thenReturn400ErrorResponse() throws Exception {
        ErrorResponse errorResponse =
                getMvc().send(POST, CREATE_URI)
                        .withJwt(adminToken())
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);
        assertRequestBodyUnreadableError(errorResponse);
    }

    @Test
    @DisplayName(
            "createAccount - when name, email or password are missing then return 400 response with message")
    void createAccount_whenNameEmailPwdMissing_thenReturn400ErrorResponse() throws Exception {
        AccountRequest requestBody = AccountRequest.builder().build();
        ErrorResponse errorResponse =
                getMvc().send(POST, CREATE_URI)
                        .withJwt(adminToken())
                        .withBody(requestBody)
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);
        assertEmptyFieldError(errorResponse, "name");
        assertEmptyFieldError(errorResponse, "email");
        assertEmptyFieldError(errorResponse, "password");
    }

    @ParameterizedTest
    @ArgumentsSource(BadEmailArgumentProvider.class)
    @DisplayName("createAccount - when email has bad format then return 400 response with message")
    void createAccount_whenEmailBadFormat_thenReturn400ErrorResponse(String email)
            throws Exception {
        AccountRequest requestBody = AccountRequest.builder().name(NAMI_NAME).email(email).build();
        ErrorResponse errorResponse =
                getMvc().send(POST, CREATE_URI)
                        .withJwt(adminToken())
                        .withBody(requestBody)
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);
        assertEmailFieldError(errorResponse);
    }

    @Test
    @DisplayName("createAccount - when name too long then return 400 response with message")
    void createAccount_whenNameTooLong_thenReturn400ErrorResponse() throws Exception {
        String longName = new String(new char[101]);
        AccountRequest requestBody = AccountRequest.builder().name(longName).build();
        ErrorResponse errorResponse =
                getMvc().send(POST, CREATE_URI)
                        .withJwt(adminToken())
                        .withBody(requestBody)
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);
        assertMaxLengthFieldError(errorResponse, "name", "100");
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
                getMvc().send(POST, CREATE_URI)
                        .withJwt(adminToken())
                        .withBody(requestBody)
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);
        assertPasswordFieldError(errorResponse);
    }

    @Test
    @DisplayName(
            "createAccount - when required data provided and user role is ADMIN then call service layer and return 200 response")
    void createAccount_whenRequiredDataProvided_thenReturn200Response() throws Exception {
        AccountRequest requestBody =
                AccountRequest.builder()
                        .name(NAMI_NAME)
                        .email(NAMI_EMAIL)
                        .password(NAMI_PWD)
                        .build();
        when(accountService.createAccount(buildNewCustomerNami())).thenReturn(buildCustomerNami());
        AccountResponse accountResponse =
                getMvc().send(POST, CREATE_URI)
                        .withJwt(adminToken())
                        .withBody(requestBody)
                        .thenExpectStatus(OK)
                        .getResponseBody(AccountResponse.class);
        assertAccountResponseNami(accountResponse);
    }

    @Test
    @DisplayName(
            "createAccount - when required data provided but user role is CUSTOMER then return 403 response")
    void createAccount_whenUserRoleIsCustomer_thenReturn403Response() throws Exception {
        AccountRequest requestBody = buildCustomerRequestNami();
        getMvc().send(POST, CREATE_URI)
                .withJwt(customerToken())
                .withBody(requestBody)
                .thenExpectStatus(FORBIDDEN);
    }

    @Test
    @DisplayName("createAccount - when not authenticated then return 403 response")
    void createAccount_whenNotAuthenticated_thenReturn403Response() throws Exception {
        AccountRequest requestBody = buildCustomerRequestNami();
        getMvc().send(POST, CREATE_URI).withBody(requestBody).thenExpectStatus(FORBIDDEN);
    }

    /*    @Test
    @DisplayName(
            "createAccount - when service Throws EntityNotFoundException then return 400 Response with message")
    void createAccount_whenServiceThrowsEntityNotFoundException_thenReturn400WithMessage()
            throws Exception {
        Account account = buildAccountNami();
        AccountRequest requestBody = buildAccountRequestNami();

        doThrow(new EntityNotFoundException(messageProvider.getMessage(), account.getEmail()))
                .when(accountService)
                .createAccount(account);

        ErrorResponse errorResponse =
                sendPostRequestExpectingStatus400(CREATE_URL, withAdminToken(), requestBody);

        assertEntityNotFoundError(errorResponse, account.getEmail());
    }*/

    // TODO: controller tests for updateAccount
}
