package drk.shopamos.rest.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import drk.shopamos.rest.controller.advice.ControllerExceptionHandler;
import drk.shopamos.rest.controller.request.AuthenticationRequest;
import drk.shopamos.rest.controller.response.AuthenticationResponse;
import drk.shopamos.rest.controller.response.ErrorResponse;
import drk.shopamos.rest.service.AuthenticationService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {AuthenticationController.class, ControllerExceptionHandler.class})
class AuthenticationControllerTest extends ControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private AuthenticationService authService;

    @Test
    @DisplayName(
            "login - when null username or passsword then return 400 response with field validation errors")
    void login_whenFieldsAreNull_thenReturn400ErrorResponse() throws Exception {
        MvcResult mvcResult =
                mockMvc.perform(
                                post("/api/v1/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        buildRequest(null, null))))
                        .andExpect(status().isBadRequest())
                        .andReturn();

        ErrorResponse errorResponse =
                objectMapper.readValue(
                        mvcResult.getResponse().getContentAsString(), ErrorResponse.class);

        assertThat(errorResponse.getFieldValidationErrors().size(), is(2));
        assertInvalidFormError(errorResponse);
        assertEmptyFieldValidation(errorResponse, "username");
        assertEmptyFieldValidation(errorResponse, "password");
    }

    @Test
    @DisplayName(
            "login - when username and password provided then call service layer and return 200 response with jwt token")
    void login_whenBadFormatEmail_thenReturn400ErrorResponse() throws Exception {
        when(authService.login(SOME_USERNAME, SOME_PASSWORD)).thenReturn(SOME_TOKEN);
        MvcResult mvcResult =
                mockMvc.perform(
                                post("/api/v1/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        buildRequest(
                                                                "username@domain.com", "abc123"))))
                        .andExpect(status().isOk())
                        .andReturn();

        AuthenticationResponse response =
                objectMapper.readValue(
                        mvcResult.getResponse().getContentAsString(), AuthenticationResponse.class);

        assertThat(response.getJwtToken(), is(SOME_TOKEN));
    }

    private AuthenticationRequest buildRequest(String username, String password) {
        return AuthenticationRequest.builder().username(username).password(password).build();
    }
}
