package drk.shopamos.rest.service.exception;

import org.springframework.http.HttpStatus;

public class EntityExistsException extends BusinessException {
    public EntityExistsException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
