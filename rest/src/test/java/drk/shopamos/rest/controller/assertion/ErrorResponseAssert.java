package drk.shopamos.rest.controller.assertion;

import static drk.shopamos.rest.mother.MessageMother.MSG_BODY_UNREADABLE;
import static drk.shopamos.rest.mother.MessageMother.MSG_CANNOT_PROMOTE;
import static drk.shopamos.rest.mother.MessageMother.MSG_CANNOT_TARGET_OTHERS;
import static drk.shopamos.rest.mother.MessageMother.MSG_FIELD_CATEGORY;
import static drk.shopamos.rest.mother.MessageMother.MSG_FIELD_EMAIL;
import static drk.shopamos.rest.mother.MessageMother.MSG_FIELD_EMPTY;
import static drk.shopamos.rest.mother.MessageMother.MSG_FIELD_MAX_LENGTH;
import static drk.shopamos.rest.mother.MessageMother.MSG_FIELD_PASSWORD;
import static drk.shopamos.rest.mother.MessageMother.MSG_FORM_FIELD;
import static drk.shopamos.rest.mother.MessageMother.MSG_INVALID_ENUM;
import static drk.shopamos.rest.mother.MessageMother.MSG_NOT_FOUND_ID;
import static drk.shopamos.rest.mother.MessageMother.MSG_PARAM_WRONG_TYPE;
import static drk.shopamos.rest.mother.MessageMother.MSG_POSITIVE_VALUE;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import drk.shopamos.rest.config.MessageProvider;
import drk.shopamos.rest.controller.response.ErrorResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Component
public class ErrorResponseAssert {
    @Autowired private MessageProvider messageProvider;

    public void enumField(
            ErrorResponse errorResponse, String value, Class<? extends Enum<?>> enumType) {

        String message =
                messageProvider.getMessage(
                        MSG_INVALID_ENUM,
                        value,
                        Arrays.asList(enumType.getEnumConstants()).toString());
        assertThat(errorResponse.getMessage(), is(message));
    }

    public void positiveField(ErrorResponse errorResponse, String fieldName) {
        String message = messageProvider.getMessage(MSG_POSITIVE_VALUE);
        assertFormFieldError(errorResponse, fieldName, message);
    }

    public void emailField(ErrorResponse errorResponse) {
        String message = messageProvider.getMessage(MSG_FIELD_EMAIL, "email");
        assertFormFieldError(errorResponse, "email", message);
    }

    public void passwordField(ErrorResponse errorResponse) {
        String message = messageProvider.getMessage(MSG_FIELD_PASSWORD);
        assertFormFieldError(errorResponse, "password", message);
    }

    public void categoryField(ErrorResponse errorResponse) {
        String message = messageProvider.getMessage(MSG_FIELD_CATEGORY);
        assertFormFieldError(errorResponse, "id", message);
    }

    public void emptyField(ErrorResponse errorResponse, String fieldName) {
        String message = messageProvider.getMessage(MSG_FIELD_EMPTY, fieldName);
        assertFormFieldError(errorResponse, fieldName, message);
    }

    public void maxLengthField(ErrorResponse errorResponse, String fieldName, String length) {
        String message =
                messageProvider.getMessageWithNamedParams(
                        MSG_FIELD_MAX_LENGTH, Map.of("max", length));
        assertFormFieldError(errorResponse, fieldName, message);
    }

    public void customerTargetingOthers(ErrorResponse errorResponse) {
        assertThat(
                errorResponse.getMessage(),
                is(messageProvider.getMessage(MSG_CANNOT_TARGET_OTHERS)));
    }

    public void customerSelfPromote(ErrorResponse errorResponse) {
        assertThat(errorResponse.getMessage(), is(messageProvider.getMessage(MSG_CANNOT_PROMOTE)));
    }

    public void invalidForm(ErrorResponse errorResponse) {
        assertThat(errorResponse.getMessage(), is(messageProvider.getMessage(MSG_FORM_FIELD)));
    }

    public void requestBodyUnreadable(ErrorResponse errorResponse) {
        assertThat(errorResponse.getMessage(), is(messageProvider.getMessage(MSG_BODY_UNREADABLE)));
    }

    public void entityNotFound(ErrorResponse errorResponse, String entityName) {
        assertThat(errorResponse.getExceptionId(), is(notNullValue()));
        assertThat(
                errorResponse.getMessage(),
                is(messageProvider.getMessage(MSG_NOT_FOUND_ID, entityName)));
    }

    public void argumentMismatch(ErrorResponse errorResponse, String value, String type) {
        assertThat(
                errorResponse.getMessage(),
                is(messageProvider.getMessage(MSG_PARAM_WRONG_TYPE, value, type)));
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
}
