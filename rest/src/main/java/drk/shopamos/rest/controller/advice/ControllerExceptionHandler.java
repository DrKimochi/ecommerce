package drk.shopamos.rest.controller.advice;

import drk.shopamos.rest.controller.response.ErrorResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {

    private final MessageSource messageSource;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    ErrorResponse handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception, Locale locale) {
        return ErrorResponse.builder()
                .message(messageSource.getMessage("error.form.field", null, locale))
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
}
