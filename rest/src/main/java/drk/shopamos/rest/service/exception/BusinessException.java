package drk.shopamos.rest.service.exception;

import java.util.UUID;

public class BusinessException extends RuntimeException {
    private final String exceptionId;

    public BusinessException(String message) {
        super(message);
        this.exceptionId = UUID.randomUUID().toString();
    }

    public String getExceptionId() {
        return this.exceptionId;
    }

    @Override
    public String toString() {
        return String.format(
                "Exception id: [%s], message: %s", this.getExceptionId(), this.getMessage());
    }
}
