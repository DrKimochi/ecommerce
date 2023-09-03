package drk.shopamos.rest.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String message;
    private List<FieldValidationError> fieldValidationErrors;

    @Data
    @AllArgsConstructor
    public static class FieldValidationError {
        private String fieldName;
        private String fieldMessage;
    }
}
