package drk.shopamos.rest.controller.advice;

import static drk.shopamos.rest.config.MessageProvider.MSG_BODY_UNREADABLE;
import static drk.shopamos.rest.config.MessageProvider.MSG_FORM_FIELD;
import static drk.shopamos.rest.config.MessageProvider.MSG_INVALID_ENUM;
import static drk.shopamos.rest.config.MessageProvider.MSG_PARAM_WRONG_TYPE;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import drk.shopamos.rest.config.MessageProvider;
import drk.shopamos.rest.controller.exception.IllegalDataException;
import drk.shopamos.rest.controller.response.ErrorResponse;
import drk.shopamos.rest.service.exception.BusinessException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        Optional<String> invalidEnum = getInvalidEnum(exception);
        String message;
        if (invalidEnum.isPresent()) {
            message =
                    messageProvider.getMessage(
                            MSG_INVALID_ENUM,
                            invalidEnum.get(),
                            getEnumValidNames(exception).toString());
        } else {
            message = messageProvider.getMessage(MSG_BODY_UNREADABLE);
        }
        return ErrorResponse.builder().message(message).build();
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    ResponseEntity<ErrorResponse> handleBusinessException(BusinessException exception) {
        return ResponseEntity.status(exception.getHttpStatus())
                .body(
                        ErrorResponse.builder()
                                .exceptionId(exception.getExceptionId())
                                .message(exception.getMessage())
                                .build());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(IllegalDataException.class)
    @ResponseBody
    ErrorResponse handleIllegalDataException(IllegalDataException exception) {
        return ErrorResponse.builder()
                .message(messageProvider.getMessage(exception.getMessageCode()))
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

    private Optional<String> getInvalidEnum(Throwable throwable) {
        return Optional.ofNullable(throwable.getCause())
                .filter(InvalidFormatException.class::isInstance)
                .map(InvalidFormatException.class::cast)
                .map(InvalidFormatException::getValue)
                .map(Object::toString);
    }

    private List<String> getEnumValidNames(Throwable throwable) {
        return Optional.ofNullable(throwable.getCause())
                .filter(InvalidFormatException.class::isInstance)
                .map(InvalidFormatException.class::cast)
                .map(InvalidFormatException::getTargetType)
                .map(Class::getEnumConstants)
                .map(enums -> Arrays.stream(enums).map(Object::toString).toList())
                .orElse(new ArrayList<>());
    }
}
