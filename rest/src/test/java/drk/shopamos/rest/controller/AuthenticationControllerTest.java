package drk.shopamos.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import drk.shopamos.rest.controller.advice.ControllerExceptionHandler;
import drk.shopamos.rest.controller.request.AuthenticationRequest;
import drk.shopamos.rest.service.AuthenticationService;

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
class AuthenticationControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private AuthenticationService authService;

    @Test
    void whenValidInput_thenReturnAStringToken() throws Exception {
        when(authService.login(any(), any())).thenReturn("abc");
        MvcResult result =
                mockMvc.perform(
                                post("/api/v1/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        buildRequest(null, null))))
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andReturn();
    }

    private AuthenticationRequest buildRequest(String username, String password) {
        return AuthenticationRequest.builder().username(username).password(password).build();
    }
}
