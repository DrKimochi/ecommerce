package drk.shopamos.rest.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static java.util.Objects.nonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import drk.shopamos.rest.controller.response.ErrorResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Optional;

public abstract class ControllerTest {
    private static final String PROPERTY_FORM_FIELD = "error.form.field";
    private static final String PROPERTY_FIELD_EMPTY = "error.form.field.empty";
    private static final String PROPERTY_FIELD_EMAIL = "error.form.field.email";
    private static final String PROPERTY_FIELD_PASSWORD = "error.form.field.password";
    private static final String PROPERTY_BODY_UNREADABLE = "error.request.body.unreadable";
    protected static String SOME_EMAIL = "username@domain.com";
    protected static String SOME_NAME = "John Doe";
    protected static String SOME_PASSWORD = "abcDEF123";
    protected static String SOME_TOKEN = "xxxxx.yyyyy.zzzzz";
    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;
    @Autowired private MessageSource messageSource;

    protected void assertEmailValidation(ErrorResponse errorResponse) {
        assertFieldErrorValidation(errorResponse, "email", PROPERTY_FIELD_EMAIL);
    }

    protected void assertPasswordValidation(ErrorResponse errorResponse) {
        assertFieldErrorValidation(errorResponse, "password", PROPERTY_FIELD_PASSWORD);
    }

    protected void assertEmptyFieldValidation(ErrorResponse errorResponse, String fieldName) {
        assertFieldErrorValidation(errorResponse, fieldName, PROPERTY_FIELD_EMPTY);
    }

    protected void assertInvalidFormError(ErrorResponse errorResponse) {
        assertThat(errorResponse.getMessage(), is(getMessageFromBundle(PROPERTY_FORM_FIELD)));
    }

    protected void assertRequestBodyUnreadableError(ErrorResponse errorResponse) {
        assertThat(errorResponse.getMessage(), is(getMessageFromBundle(PROPERTY_BODY_UNREADABLE)));
    }

    protected ErrorResponse readErrorResponse(MvcResult mvcResult)
            throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
    }

    protected MvcResult postMvcRequestExpectingStatus400(String url, Object body) throws Exception {
        return mockMvc.perform(getPostRequestBuilder(url, body))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    protected MvcResult postMvcRequestExpectingStatus200(String url, Object body) throws Exception {
        return mockMvc.perform(getPostRequestBuilder(url, body))
                .andExpect(status().isOk())
                .andReturn();
    }

    private MockHttpServletRequestBuilder getPostRequestBuilder(String url, Object body)
            throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
                post(url).contentType(MediaType.APPLICATION_JSON);
        if (nonNull(body)) {
            requestBuilder = requestBuilder.content(objectMapper.writeValueAsString(body));
        }
        return requestBuilder;
    }

    private void assertFieldErrorValidation(
            ErrorResponse errorResponse, String fieldName, String fieldMsgProperty) {

        Optional<ErrorResponse.FieldValidationError> fieldError =
                findFieldValidationError(errorResponse, fieldName);

        assertThat(fieldError.isPresent(), is(true));

        assertThat(fieldError.get().getFieldMessage(), is(getMessageFromBundle(fieldMsgProperty)));
    }

    private Optional<ErrorResponse.FieldValidationError> findFieldValidationError(
            ErrorResponse errorResponse, String fieldName) {
        return errorResponse.getFieldValidationErrors().stream()
                .filter(
                        fieldValidationError ->
                                fieldValidationError.getFieldName().equals(fieldName))
                .findFirst();
    }

    private String getMessageFromBundle(String propertyName) {
        return messageSource.getMessage(propertyName, null, Locale.getDefault());
    }
}
