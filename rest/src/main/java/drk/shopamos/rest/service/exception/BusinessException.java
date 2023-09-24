package drk.shopamos.rest.service.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class BusinessException extends RuntimeException {
    private final String exceptionId;
    private final HttpStatus httpStatus;

    public BusinessException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.exceptionId = UUID.randomUUID().toString();
    }

    public String getExceptionId() {
        return this.exceptionId;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String toString() {
        return String.format(
                "Exception id: [%s], message: %s", this.getExceptionId(), this.getMessage());
    }
}
