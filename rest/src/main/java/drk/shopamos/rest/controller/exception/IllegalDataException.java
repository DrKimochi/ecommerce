package drk.shopamos.rest.controller.exception;

public class IllegalDataException extends RuntimeException {
    private final String messageCode;

    public IllegalDataException(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }
}
