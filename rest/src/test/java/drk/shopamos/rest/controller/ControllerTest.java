package drk.shopamos.rest.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static java.util.Objects.nonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import drk.shopamos.rest.config.MessageProvider;
import drk.shopamos.rest.controller.response.ErrorResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

public abstract class ControllerTest {
    private static final String MSG_FORM_FIELD = "error.form.field";
    private static final String MSG_FIELD_EMPTY = "error.form.field.empty";
    private static final String MSG_FIELD_EMAIL = "error.form.field.email";
    private static final String MSG_FIELD_PASSWORD = "error.form.field.password";
    private static final String MSG_BODY_UNREADABLE = "error.request.body.unreadable";
    private static final String MSG_ENTITY_NOT_FOUND = "error.business.entity.notfound";
    protected static String SOME_TOKEN = "xxxxx.yyyyy.zzzzz";
    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;
    @Autowired protected MessageProvider messageProvider;

    protected void assertEmailValidation(ErrorResponse errorResponse) {
        assertFieldErrorValidation(errorResponse, "email", MSG_FIELD_EMAIL);
    }

    protected void assertPasswordValidation(ErrorResponse errorResponse) {
        assertFieldErrorValidation(errorResponse, "password", MSG_FIELD_PASSWORD);
    }

    protected void assertEmptyFieldValidation(ErrorResponse errorResponse, String fieldName) {
        assertFieldErrorValidation(errorResponse, fieldName, MSG_FIELD_EMPTY);
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

    protected ErrorResponse postMvcRequestExpectingStatus400(String url, Object body)
            throws Exception {
        return readErrorResponse(
                mockMvc.perform(getPostRequestBuilder(url, body))
                        .andExpect(status().isBadRequest())
                        .andReturn());
    }

    protected MvcResult postMvcRequestExpectingStatus200(String url, Object body) throws Exception {
        return mockMvc.perform(getPostRequestBuilder(url, body))
                .andExpect(status().isOk())
                .andReturn();
    }

    private ErrorResponse readErrorResponse(MvcResult mvcResult)
            throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
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

        assertThat(
                fieldError.get().getFieldMessage(),
                is(messageProvider.getMessage(fieldMsgProperty)));
    }

    private Optional<ErrorResponse.FieldValidationError> findFieldValidationError(
            ErrorResponse errorResponse, String fieldName) {
        return errorResponse.getFieldValidationErrors().stream()
                .filter(
                        fieldValidationError ->
                                fieldValidationError.getFieldName().equals(fieldName))
                .findFirst();
    }
}
