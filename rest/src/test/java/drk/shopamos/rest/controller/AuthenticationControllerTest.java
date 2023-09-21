package drk.shopamos.rest.controller;

import static drk.shopamos.rest.mother.AccountMother.NAMI_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.NAMI_PWD;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import com.fasterxml.jackson.core.JsonProcessingException;

import drk.shopamos.rest.controller.request.AuthenticationRequest;
import drk.shopamos.rest.controller.response.AuthenticationResponse;
import drk.shopamos.rest.controller.response.ErrorResponse;
import drk.shopamos.rest.service.AuthenticationService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

@WebMvcTest
@ContextConfiguration(classes = {AuthenticationController.class})
class AuthenticationControllerTest extends ControllerTest {
    private static final String LOGIN_URL = "/v1/auth/login";
    @MockBean private AuthenticationService authService;

    @Test
    @DisplayName("login - when body is missing then return 400 response with message")
    void login_whenBodyMissing_thenReturn400ErrorResponse() throws Exception {
        ErrorResponse errorResponse =
                getMvc().send(POST, LOGIN_URL)
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);

        assertRequestBodyUnreadableError(errorResponse);
    }

    @Test
    @DisplayName(
            "login - when null username or password then return 400 response with field validation errors")
    void login_whenFieldsAreNull_thenReturn400ErrorResponse() throws Exception {
        AuthenticationRequest body = buildRequest(null, null);

        ErrorResponse errorResponse =
                getMvc().send(POST, LOGIN_URL)
                        .withBody(body)
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);

        assertThat(errorResponse.getFieldValidationErrors().size(), is(2));
        assertInvalidFormError(errorResponse);
        assertEmptyFieldError(errorResponse, "username");
        assertEmptyFieldError(errorResponse, "password");
    }

    @Test
    @DisplayName(
            "login - when username and password provided then call service layer and return 200 response with jwt token")
    void login_whenRequiredDataProvided_thenReturn200Response() throws Exception {
        when(authService.login(NAMI_EMAIL, NAMI_PWD)).thenReturn(SOME_TOKEN);

        AuthenticationRequest body = buildRequest(NAMI_EMAIL, NAMI_PWD);
        AuthenticationResponse response =
                getMvc().send(POST, LOGIN_URL)
                        .withBody(body)
                        .thenExpectStatus(OK)
                        .getResponseBody(AuthenticationResponse.class);

        assertThat(response.getJwtToken(), is(SOME_TOKEN));
    }

    private AuthenticationRequest buildRequest(String username, String password) {
        return AuthenticationRequest.builder().username(username).password(password).build();
    }

}
