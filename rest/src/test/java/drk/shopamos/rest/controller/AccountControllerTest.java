package drk.shopamos.rest.controller;

import static drk.shopamos.rest.mother.AccountMother.NAMI_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.NAMI_ID;
import static drk.shopamos.rest.mother.AccountMother.NAMI_NAME;
import static drk.shopamos.rest.mother.AccountMother.NAMI_PWD;
import static drk.shopamos.rest.mother.AccountMother.buildCustomerNamiWithId;
import static drk.shopamos.rest.mother.AccountMother.buildCustomerNamiWithoutId;
import static drk.shopamos.rest.mother.AccountMother.buildCustomerRequestNami;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

import drk.shopamos.rest.argument.AccountCreateUpdateUriArguments;
import drk.shopamos.rest.argument.BadEmailArguments;
import drk.shopamos.rest.argument.BadPasswordArguments;
import drk.shopamos.rest.controller.mapper.AccountMapperImpl;
import drk.shopamos.rest.controller.request.AccountRequest;
import drk.shopamos.rest.controller.response.AccountResponse;
import drk.shopamos.rest.controller.response.ErrorResponse;
import drk.shopamos.rest.model.entity.Account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

@WebMvcTest
@ContextConfiguration(classes = {AccountController.class, AccountMapperImpl.class})
public final class AccountControllerTest extends ControllerTest {

    public static final String CREATE_URI = "/v1/accounts";
    public static final String UPDATE_URI = "/v1/accounts/{id}";
    @Autowired PasswordEncoder passwordEncoder;

    @ParameterizedTest
    @ArgumentsSource(AccountCreateUpdateUriArguments.class)
    @DisplayName("createUpdateAccount - when body is missing then return 400 with message")
    void createUpdateAccount_whenBodyMissing_thenReturn400(
            HttpMethod httpMethod, String uri, String uriVariable) throws Exception {
        ErrorResponse errorResponse =
                getMvc().send(httpMethod, uri, uriVariable)
                        .withJwt(adminToken())
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);
        assertRequestBodyUnreadableError(errorResponse);
    }

    @ParameterizedTest
    @ArgumentsSource(AccountCreateUpdateUriArguments.class)
    @DisplayName(
            "createUpdateAccount - when name, email or password are missing then return 400 with message")
    void createUpdateAccount_whenNameEmailPwdMissing_thenReturn400(
            HttpMethod httpMethod, String uri, String uriVariable) throws Exception {
        AccountRequest requestBody = AccountRequest.builder().build();
        ErrorResponse errorResponse =
                getMvc().send(httpMethod, uri, uriVariable)
                        .withJwt(adminToken())
                        .withBody(requestBody)
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);
        assertEmptyFieldError(errorResponse, "name");
        assertEmptyFieldError(errorResponse, "email");
        assertEmptyFieldError(errorResponse, "password");
    }

    @ParameterizedTest
    @ArgumentsSource(BadEmailArguments.class)
    @DisplayName("createUpdateAccount - when email has bad format then return 400 with message")
    void createUpdateAccount_whenEmailBadFormat_thenReturn400(
            HttpMethod httpMethod, String uri, String uriVariable, String email) throws Exception {
        AccountRequest requestBody = AccountRequest.builder().name(NAMI_NAME).email(email).build();
        ErrorResponse errorResponse =
                getMvc().send(httpMethod, uri, uriVariable)
                        .withJwt(adminToken())
                        .withBody(requestBody)
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);
        assertEmailFieldError(errorResponse);
    }

    @ParameterizedTest
    @ArgumentsSource(AccountCreateUpdateUriArguments.class)
    @DisplayName("createUpdateAccount - when name too long then return 400 with message")
    void createUpdateAccount_whenNameTooLong_thenReturn400(
            HttpMethod httpMethod, String uri, String uriVariable) throws Exception {
        String longName = new String(new char[101]);
        AccountRequest requestBody = AccountRequest.builder().name(longName).build();
        ErrorResponse errorResponse =
                getMvc().send(httpMethod, uri, uriVariable)
                        .withJwt(adminToken())
                        .withBody(requestBody)
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);
        assertMaxLengthFieldError(errorResponse, "name", "100");
    }

    @ParameterizedTest
    @ArgumentsSource(BadPasswordArguments.class)
    @DisplayName("createUpdateAccount - when password has bad format then return 400 with message")
    void createUpdateAccount_whenPasswordBadFormat_thenReturn400(
            HttpMethod httpMethod, String uri, String uriVariable, String password)
            throws Exception {
        AccountRequest requestBody =
                AccountRequest.builder()
                        .name(NAMI_NAME)
                        .email(NAMI_EMAIL)
                        .password(password)
                        .build();

        ErrorResponse errorResponse =
                getMvc().send(httpMethod, uri, uriVariable)
                        .withJwt(adminToken())
                        .withBody(requestBody)
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);
        assertPasswordFieldError(errorResponse);
    }

    @Test
    @DisplayName(
            "createAccount - when required data provided and user role is ADMIN then call service layer and return 200")
    void createAccount_whenRequiredDataProvided_thenReturn200() throws Exception {
        when(accountService.createAccount(buildCustomerNamiWithoutId()))
                .thenReturn(buildCustomerNamiWithId());
        AccountRequest requestBody =
                AccountRequest.builder()
                        .name(NAMI_NAME)
                        .email(NAMI_EMAIL)
                        .password(NAMI_PWD)
                        .build();
        AccountResponse accountResponse =
                getMvc().send(POST, CREATE_URI)
                        .withJwt(adminToken())
                        .withBody(requestBody)
                        .thenExpectStatus(OK)
                        .getResponseBody(AccountResponse.class);
        assertAccountResponseNami(accountResponse);
    }

    @ParameterizedTest
    @ArgumentsSource(AccountCreateUpdateUriArguments.class)
    @DisplayName(
            "createUpdateAccount - when required data provided but user role is CUSTOMER then return 403")
    void createUpdateAccount_whenUserRoleIsCustomer_thenReturn403(
            HttpMethod httpMethod, String uri, String uriVariable) throws Exception {
        AccountRequest requestBody = buildCustomerRequestNami();
        getMvc().send(httpMethod, uri, uriVariable)
                .withJwt(customerToken())
                .withBody(requestBody)
                .thenExpectStatus(FORBIDDEN);
    }

    @ParameterizedTest
    @ArgumentsSource(AccountCreateUpdateUriArguments.class)
    @DisplayName("createUpdateAccount - when not authenticated then return 403 response")
    void createUpdateAccount_whenNotAuthenticated_thenReturn403(
            HttpMethod httpMethod, String uri, String uriVariable) throws Exception {
        AccountRequest requestBody = buildCustomerRequestNami();
        getMvc().send(httpMethod, uri, uriVariable)
                .withBody(requestBody)
                .thenExpectStatus(FORBIDDEN);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2147483648", "12A"})
    @DisplayName(
            "updateAccount - when id cannot be converted to integer then return 400 with message")
    void updateAccount_whenInvalidIdType_thenReturn400(String id) throws Exception {
        ErrorResponse errorResponse =
                getMvc().send(PUT, UPDATE_URI, id)
                        .withJwt(adminToken())
                        .withBody(new AccountRequest())
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);
        assertArgumentMismatchError(errorResponse, "id", "integer");
    }

    @Test
    @DisplayName(
            "updateAccount - when required data provided then call service layer and return 200 with updated account info")
    void updateAccount_whenRequiredDataProvided_thenReturn200() throws Exception {
        Account nami = buildCustomerNamiWithId();
        when(accountService.updateAccount(nami)).thenReturn(nami);
        AccountRequest requestBody =
                AccountRequest.builder()
                        .name(NAMI_NAME)
                        .email(NAMI_EMAIL)
                        .password(NAMI_PWD)
                        .build();
        AccountResponse accountResponse =
                getMvc().send(PUT, UPDATE_URI, NAMI_ID)
                        .withJwt(adminToken())
                        .withBody(requestBody)
                        .thenExpectStatus(OK)
                        .getResponseBody(AccountResponse.class);
        assertAccountResponseNami(accountResponse);
    }

    private void assertAccountResponseNami(AccountResponse accountResponse) {
        assertThat(accountResponse.getEmail(), is(NAMI_EMAIL));
        assertThat(accountResponse.getId(), is(NAMI_ID));
        assertThat(accountResponse.getName(), is(NAMI_NAME));
        assertThat(accountResponse.getAdmin(), is(false));
        assertThat(accountResponse.getActive(), is(true));
    }
}
