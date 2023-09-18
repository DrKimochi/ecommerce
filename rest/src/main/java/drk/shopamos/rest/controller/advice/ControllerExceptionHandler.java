package drk.shopamos.rest.controller.advice;

import static drk.shopamos.rest.config.MessageProvider.MSG_BODY_UNREADABLE;
import static drk.shopamos.rest.config.MessageProvider.MSG_FORM_FIELD;

import drk.shopamos.rest.config.MessageProvider;
import drk.shopamos.rest.controller.response.ErrorResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {
    private final MessageProvider messageProvider;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return ErrorResponse.builder()
                .message(messageProvider.getMessage(MSG_FORM_FIELD))
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
    ErrorResponse handleHttpMessageNotReadableException() {
        return ErrorResponse.builder()
                .message(messageProvider.getMessage(MSG_BODY_UNREADABLE))
                .build();
    }
}
