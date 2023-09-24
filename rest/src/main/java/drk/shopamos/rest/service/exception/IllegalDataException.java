package drk.shopamos.rest.service.exception;

import org.springframework.http.HttpStatus;

public class IllegalDataException extends BusinessException {

    public IllegalDataException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
