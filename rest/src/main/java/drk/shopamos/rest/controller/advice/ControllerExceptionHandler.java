package drk.shopamos.rest.controller.advice;

import drk.shopamos.rest.controller.response.ErrorResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {
    private static final String PROPERTY_FORM_FIELD = "error.form.field";
    private static final String PROPERTY_BODY_UNREADABLE = "error.request.body.unreadable";
    private final MessageSource messageSource;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    ErrorResponse handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception, Locale locale) {
        return ErrorResponse.builder()
                .message(messageSource.getMessage(PROPERTY_FORM_FIELD, null, locale))
                .fieldValidationErrors(
                        exception.getFieldErrors().stream()
                                .map(
                                        fieldError ->
                                                new ErrorResponse.FieldValidationError(
                                                        fieldError.getField(),
                                                        fieldError.getDefaultMessage()))
                                .toList())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    ErrorResponse handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception, Locale locale) {
        return ErrorResponse.builder()
                .message(messageSource.getMessage(PROPERTY_BODY_UNREADABLE, null, locale))
                .build();
    }
}
