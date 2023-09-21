package drk.shopamos.rest.controller.advice;

import static drk.shopamos.rest.config.MessageProvider.MSG_BODY_UNREADABLE;
import static drk.shopamos.rest.config.MessageProvider.MSG_FORM_FIELD;
import static drk.shopamos.rest.config.MessageProvider.MSG_PARAM_WRONG_TYPE;

import static java.util.Objects.requireNonNull;

import drk.shopamos.rest.config.MessageProvider;
import drk.shopamos.rest.controller.response.ErrorResponse;
import drk.shopamos.rest.service.exception.BusinessException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    ErrorResponse handleBusinessException(BusinessException exception) {
        return ErrorResponse.builder()
                .exceptionId(exception.getExceptionId())
                .message(exception.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    ErrorResponse handleArgumentMismatchException(MethodArgumentTypeMismatchException exception) {
        String value = exception.getName();
        String type = requireNonNull(exception.getRequiredType()).getSimpleName().toLowerCase();
        return ErrorResponse.builder()
                .message(messageProvider.getMessage(MSG_PARAM_WRONG_TYPE, value, type))
                .build();
    }
}
