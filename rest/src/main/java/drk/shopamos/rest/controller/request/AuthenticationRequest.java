package drk.shopamos.rest.controller.request;

import jakarta.validation.constraints.Email;
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
    @Email(message = "{error.form.field.email}")
    @NotEmpty(message = "{error.form.field.notEmpty}")
    private String username;

    @NotEmpty(message = "{error.form.field.notEmpty}")
    private String password;
}
