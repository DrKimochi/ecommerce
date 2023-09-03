package drk.shopamos.rest.controller.request;

import jakarta.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NotEmpty(message = "{error.form.field.empty}")
    private String username;

    @NotEmpty(message = "{error.form.field.empty}")
    private String password;
}
