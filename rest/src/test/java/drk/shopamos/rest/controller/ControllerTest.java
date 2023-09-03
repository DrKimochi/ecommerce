package drk.shopamos.rest.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import drk.shopamos.rest.controller.response.ErrorResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.Optional;

public abstract class ControllerTest {
    protected static String SOME_USERNAME = "username@domain.com";
    protected static String SOME_PASSWORD = "abc123";
    protected static String SOME_TOKEN = "xxxxx.yyyyy.zzzzz";

    protected static String PROPERTY_FORM_FIELD = "error.form.field";
    protected static String PROPERTY_FIELD_EMPTY = "error.form.field.empty";
    protected static String PROPERTY_FIELD_EMAIL = "error.form.field.email";
    @Autowired private MessageSource messageSource;

    protected void asserEmailValidation(ErrorResponse errorResponse, String fieldName) {
        assertFieldErrorValidation(errorResponse, fieldName, PROPERTY_FIELD_EMAIL);
    }

    protected void assertEmptyFieldValidation(ErrorResponse errorResponse, String fieldName) {
        assertFieldErrorValidation(errorResponse, fieldName, PROPERTY_FIELD_EMPTY);
    }

    protected void assertInvalidFormError(ErrorResponse errorResponse) {
        assertThat(errorResponse.getMessage(), is(getMessageFromBundle(PROPERTY_FORM_FIELD)));
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