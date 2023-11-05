package drk.shopamos.rest.service.exception;

import org.springframework.http.HttpStatus;

public class BadDataException extends BusinessException {
    public BadDataException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
