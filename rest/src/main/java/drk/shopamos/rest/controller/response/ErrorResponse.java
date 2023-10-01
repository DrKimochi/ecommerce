package drk.shopamos.rest.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String exceptionId;
    private String message;
    private List<FieldValidationError> fieldValidationErrors;

    @Getter
    @AllArgsConstructor
    public static class FieldValidationError {
        private String fieldName;
        private String fieldMessage;
    }
}
