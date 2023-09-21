package drk.shopamos.rest.controller;

import static drk.shopamos.rest.mother.AccountMother.LUFFY_EMAIL;
import static drk.shopamos.rest.service.ServiceTest.MSG_BODY_UNREADABLE;
import static drk.shopamos.rest.service.ServiceTest.MSG_FIELD_EMAIL;
import static drk.shopamos.rest.service.ServiceTest.MSG_FIELD_EMPTY;
import static drk.shopamos.rest.service.ServiceTest.MSG_FIELD_MAX_LENGTH;
import static drk.shopamos.rest.service.ServiceTest.MSG_FIELD_PASSWORD;
import static drk.shopamos.rest.service.ServiceTest.MSG_FORM_FIELD;
import static drk.shopamos.rest.service.ServiceTest.MSG_NOT_FOUND_ID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;

import drk.shopamos.rest.config.JwtAuthenticationFilter;
import drk.shopamos.rest.config.JwtTokenHelper;
import drk.shopamos.rest.config.MessageProvider;
import drk.shopamos.rest.config.SecurityConfiguration;
import drk.shopamos.rest.config.ShopamosConfiguration;
import drk.shopamos.rest.controller.advice.ControllerExceptionHandler;
import drk.shopamos.rest.controller.response.ErrorResponse;
import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.service.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

@ContextConfiguration(
        classes = {
            ControllerExceptionHandler.class,
            MessageProvider.class,
            ShopamosConfiguration.class,
            SecurityConfiguration.class,
            JwtAuthenticationFilter.class,
            JwtTokenHelper.class
        })
public abstract class ControllerTest {

    protected static String SOME_TOKEN = "xxxxx.yyyyy.zzzzz";

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired protected MessageProvider messageProvider;
    @Autowired protected JwtTokenHelper jwtTokenHelper;
    @MockBean protected AccountService accountService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    protected void assertEmailFieldError(ErrorResponse errorResponse) {
        String message = messageProvider.getMessage(MSG_FIELD_EMAIL, "email");
        assertFormFieldError(errorResponse, "email", message);
    }

    protected void assertPasswordFieldError(ErrorResponse errorResponse) {
        String message = messageProvider.getMessage(MSG_FIELD_PASSWORD, "password");
        assertFormFieldError(errorResponse, "password", message);
    }

    protected void assertEmptyFieldError(ErrorResponse errorResponse, String fieldName) {
        String message = messageProvider.getMessage(MSG_FIELD_EMPTY, fieldName);
        assertFormFieldError(errorResponse, fieldName, message);
    }

    protected void assertMaxLengthFieldError(
            ErrorResponse errorResponse, String fieldName, String length) {
        String message =
                messageProvider.getMessageWithNamedParams(
                        MSG_FIELD_MAX_LENGTH, Map.of("max", length));
        assertFormFieldError(errorResponse, fieldName, message);
    }

    protected void assertInvalidFormError(ErrorResponse errorResponse) {
        assertThat(errorResponse.getMessage(), is(messageProvider.getMessage(MSG_FORM_FIELD)));
    }

    protected void assertRequestBodyUnreadableError(ErrorResponse errorResponse) {
        assertThat(errorResponse.getMessage(), is(messageProvider.getMessage(MSG_BODY_UNREADABLE)));
    }

    protected void assertEntityNotFoundError(ErrorResponse errorResponse, String entityName) {
        assertThat(errorResponse.getExceptionId(), is(notNullValue()));
        assertThat(
                errorResponse.getMessage(),
                is(messageProvider.getMessage(MSG_NOT_FOUND_ID, entityName)));
    }

    public MockMvcHandler getMvc() {
        return new MockMvcHandler(mockMvc, objectMapper);
    }

    private void assertFormFieldError(
            ErrorResponse errorResponse, String fieldName, String message) {

        Optional<ErrorResponse.FieldValidationError> fieldError =
                findFieldValidationError(errorResponse, fieldName);

        assertThat(fieldError.isPresent(), is(true));

        assertThat(fieldError.get().getFieldMessage(), is(message));
    }

    private Optional<ErrorResponse.FieldValidationError> findFieldValidationError(
            ErrorResponse errorResponse, String fieldName) {
        return errorResponse.getFieldValidationErrors().stream()
                .filter(
                        fieldValidationError ->
                                fieldValidationError.getFieldName().equals(fieldName))
                .findFirst();
    }

    protected String adminToken() {
        return getJwtToken(true);
    }

    protected String customerToken() {
        return getJwtToken(false);
    }

    private String getJwtToken(boolean isAdmin) {
        Account account = new Account();
        account.setEmail(LUFFY_EMAIL);
        account.setAdmin(isAdmin);
        when(accountService.loadUserByUsername(LUFFY_EMAIL)).thenReturn(account);
        return jwtTokenHelper.generateToken(account);
    }
}
