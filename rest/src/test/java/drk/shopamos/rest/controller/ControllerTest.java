package drk.shopamos.rest.controller;

import static drk.shopamos.rest.mother.AccountMother.LUFFY_EMAIL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static java.util.Objects.nonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.UnsupportedEncodingException;
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
    private static final String MSG_FORM_FIELD = "error.form.field";
    private static final String MSG_FIELD_EMPTY = "error.form.field.empty";
    private static final String MSG_FIELD_EMAIL = "error.form.field.email";
    private static final String MSG_FIELD_PASSWORD = "error.form.field.password";
    private static final String MSG_FIELD_MAX_LENGTH = "error.form.field.maxlength";
    private static final String MSG_BODY_UNREADABLE = "error.request.body.unreadable";
    private static final String MSG_ENTITY_NOT_FOUND = "error.business.entity.notfound";
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
                is(messageProvider.getMessage(MSG_ENTITY_NOT_FOUND, entityName)));
    }

    protected ErrorResponse sendPostRequestExpectingStatus400(
            String url, String jwtToken, Object body) throws Exception {
        return readErrorResponse(
                mockMvc.perform(getPostRequestBuilder(url, jwtToken, body))
                        .andExpect(status().isBadRequest())
                        .andReturn());
    }

    protected MvcResult sendPostRequestExpectingStatus200(String url, String jwtToken, Object body)
            throws Exception {
        return mockMvc.perform(getPostRequestBuilder(url, jwtToken, body))
                .andExpect(status().isOk())
                .andReturn();
    }

    protected MvcResult sendPostRequestExpectingStatus403(String url, String jwtToken, Object body)
            throws Exception {
        return mockMvc.perform(getPostRequestBuilder(url, jwtToken, body))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    private ErrorResponse readErrorResponse(MvcResult mvcResult)
            throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
    }

    private MockHttpServletRequestBuilder getPostRequestBuilder(
            String url, String jwtToken, Object body) throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
                post(url).contentType(MediaType.APPLICATION_JSON);
        if (nonNull(jwtToken)) {
            requestBuilder = requestBuilder.header("authorization", "Bearer " + jwtToken);
        }
        if (nonNull(body)) {
            requestBuilder = requestBuilder.content(objectMapper.writeValueAsString(body));
        }
        return requestBuilder;
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

    protected String withAdminToken() {
        return getJwtToken(true);
    }

    protected String withCustomerToken() {
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
