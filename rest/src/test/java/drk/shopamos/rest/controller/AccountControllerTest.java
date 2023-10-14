package drk.shopamos.rest.controller;

import static drk.shopamos.rest.mother.AccountMother.LUFFY_ID;
import static drk.shopamos.rest.mother.AccountMother.NAMI_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.NAMI_ID;
import static drk.shopamos.rest.mother.AccountMother.NAMI_NAME;
import static drk.shopamos.rest.mother.AccountMother.buildCustomerNamiWithId;
import static drk.shopamos.rest.mother.AccountMother.buildCustomerNamiWithoutId;
import static drk.shopamos.rest.mother.AccountMother.buildCustomerRequestNami;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

import drk.shopamos.rest.argument.AccountCreateUpdateUriArguments;
import drk.shopamos.rest.argument.AccountDeleteUpdateUriArguments;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

@WebMvcTest
@ContextConfiguration(classes = {AccountController.class, AccountMapperImpl.class})
public final class AccountControllerTest extends ControllerTest {

    public static final String CREATE_URI = "/v1/accounts";
    public static final String DELETE_UPDATE_URI = "/v1/accounts/{id}";
    @Autowired PasswordEncoder passwordEncoder;

    @ParameterizedTest
    @ArgumentsSource(AccountCreateUpdateUriArguments.class)
    @DisplayName("createUpdateAccount - when body is missing then return 400 with message")
    void createUpdateAccount_whenBodyMissing_thenReturn400(
            HttpMethod httpMethod, String uri, String uriVariable) throws Exception {
        ErrorResponse errorResponse =
                getMvc().send(httpMethod, uri, uriVariable)
                        .withJwt(adminToken(LUFFY_ID))
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
                        .withJwt(adminToken(LUFFY_ID))
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
                        .withJwt(adminToken(LUFFY_ID))
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
                        .withJwt(adminToken(LUFFY_ID))
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
                        .withJwt(adminToken(LUFFY_ID))
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
        AccountRequest requestBody = buildCustomerRequestNami();
        getMvc().send(POST, CREATE_URI)
                .withJwt(adminToken(NAMI_ID))
                .withBody(requestBody)
                .thenExpectStatus(OK);
    }

    @Test
    @DisplayName("createAccount - unauthenticated users can create accounts")
    void createAccount_whenUnauthenticatedUser_thenReturn200() throws Exception {
        when(accountService.createAccount(buildCustomerNamiWithoutId()))
                .thenReturn(buildCustomerNamiWithId());
        AccountRequest requestBody = buildCustomerRequestNami();
        getMvc().send(POST, CREATE_URI).withBody(requestBody).thenExpectStatus(OK);
    }

    @Test
    @DisplayName(
            "createAccount -when 200 response is returned then body containing new account info is also returned")
    void createAccount_when200Response_thenBodyContainsNewAccountInfo() throws Exception {
        when(accountService.createAccount(buildCustomerNamiWithoutId()))
                .thenReturn(buildCustomerNamiWithId());
        AccountRequest requestBody = buildCustomerRequestNami();
        AccountResponse accountResponse =
                getMvc().send(POST, CREATE_URI)
                        .withJwt(adminToken(NAMI_ID))
                        .withBody(requestBody)
                        .thenExpectStatus(OK)
                        .getResponseBody(AccountResponse.class);

        assertThat(accountResponse.getId(), is(NAMI_ID));
        assertThat(accountResponse.getActive(), is(true));
        assertThat(accountResponse.getAdmin(), is(false));
        assertThat(accountResponse.getName(), is(NAMI_NAME));
    }

    @ParameterizedTest
    @ArgumentsSource(AccountDeleteUpdateUriArguments.class)
    @DisplayName(
            "deleteUpdateAccount - when id cannot be converted to integer then return 400 with message")
    void deleteUpdateAccount_whenInvalidIdType_thenReturn400(
            HttpMethod httpMethod, String uri, String id) throws Exception {
        ErrorResponse errorResponse =
                getMvc().send(httpMethod, uri, id)
                        .withJwt(adminToken(LUFFY_ID))
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);
        assertArgumentMismatchError(errorResponse, "id", "integer");
    }

    @Test
    @DisplayName(
            "updateAccount - when customer is trying to promote himself to admin then return 403 with message")
    void updateAccount_whenCustomerSelfPromote_thenReturn403() throws Exception {
        AccountRequest requestBody = buildCustomerRequestNami();
        requestBody.setAdmin(true);
        ErrorResponse errorResponse =
                getMvc().send(PUT, DELETE_UPDATE_URI, NAMI_ID)
                        .withJwt(customerToken(NAMI_ID))
                        .withBody(requestBody)
                        .thenExpectStatus(FORBIDDEN)
                        .getResponseBody(ErrorResponse.class);

        assertCustomerSelfPromoteError(errorResponse);
    }

    @ParameterizedTest
    @ArgumentsSource(AccountDeleteUpdateUriArguments.class)
    @DisplayName(
            "deleteUpdateAccount - when customer is trying to delete or update some other account then return 403 with message")
    void deleteUpdateAccount_whenCustomerTargetOtherAccount_thenReturn403(
            HttpMethod httpMethod, String uri) throws Exception {
        AccountRequest requestBody = buildCustomerRequestNami();
        ErrorResponse errorResponse =
                getMvc().send(httpMethod, uri, LUFFY_ID)
                        .withJwt(customerToken(NAMI_ID))
                        .withBody(requestBody)
                        .thenExpectStatus(FORBIDDEN)
                        .getResponseBody(ErrorResponse.class);

        assertCustomerTargetingOthersError(errorResponse);
    }

    @Test
    @DisplayName(
            "updateAccount - when customer updates himself with valid data then call service layer and return 200")
    void updateAccount_whenCustomerUpdatesHimself_thenReturn200() throws Exception {
        Account nami = buildCustomerNamiWithId();
        when(accountService.updateAccount(nami)).thenReturn(nami);
        AccountRequest requestBody = buildCustomerRequestNami();
        getMvc().send(PUT, DELETE_UPDATE_URI, NAMI_ID)
                .withJwt(customerToken(NAMI_ID))
                .withBody(requestBody)
                .thenExpectStatus(OK);
    }

    @Test
    @DisplayName(
            "updateAccount - when admin updates accounts with valid data then call service layer and return 200")
    void updateAccount_whenAdminUpdatesOthers_thenReturn200() throws Exception {
        Account nami = buildCustomerNamiWithId();
        when(accountService.updateAccount(nami)).thenReturn(nami);
        AccountRequest requestBody = buildCustomerRequestNami();
        requestBody.setAdmin(true);
        requestBody.setActive(false);
        getMvc().send(PUT, DELETE_UPDATE_URI, NAMI_ID)
                .withJwt(adminToken(LUFFY_ID))
                .withBody(requestBody)
                .thenExpectStatus(OK);
    }

    @Test
    @DisplayName(
            "updateAccount - when 200 response is returned, then body containing updated account info is also returned")
    void updateAccount_when200Response_thenReturnBodyWithUpdatedAccountInfo() throws Exception {
        Account nami = buildCustomerNamiWithId();
        when(accountService.updateAccount(nami)).thenReturn(nami);
        AccountRequest requestBody = buildCustomerRequestNami();
        AccountResponse accountResponse =
                getMvc().send(PUT, DELETE_UPDATE_URI, NAMI_ID)
                        .withJwt(adminToken(LUFFY_ID))
                        .withBody(requestBody)
                        .thenExpectStatus(OK)
                        .getResponseBody(AccountResponse.class);

        assertThat(accountResponse.getName(), is(NAMI_NAME));
        assertThat(accountResponse.getId(), is(NAMI_ID));
        assertThat(accountResponse.getActive(), is(true));
        assertThat(accountResponse.getAdmin(), is(false));
    }

    @Test
    @DisplayName(
            "deleteAccount - when user is admin then call service layer and return 200 response")
    void deleteAccount_whenUserIsAdmin_thenReturn200() throws Exception {
        getMvc().send(DELETE, DELETE_UPDATE_URI, NAMI_ID)
                .withJwt(adminToken(LUFFY_ID))
                .thenExpectStatus(OK);
        verify(accountService).deleteAccount(NAMI_ID);
    }

    @Test
    @DisplayName(
            "deleteAccount - when user is customer deleting himself then call service layer and return 200 response")
    void deleteAccount_whenUserIsCustomer_thenReturn200() throws Exception {
        getMvc().send(DELETE, DELETE_UPDATE_URI, NAMI_ID)
                .withJwt(customerToken(NAMI_ID))
                .thenExpectStatus(OK);
        verify(accountService).deleteAccount(NAMI_ID);
    }
}
