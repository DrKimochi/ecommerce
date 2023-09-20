package drk.shopamos.rest.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {

    @NotEmpty(message = "{error.form.field.empty}")
    @Size(max = 100, message = "{error.form.field.maxlength}")
    private String name;

    @NotEmpty(message = "{error.form.field.empty}")
    @Email(message = "{error.form.field.email}")
    private String email;

    @NotEmpty(message = "{error.form.field.empty}")
    @Pattern(
            regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,16}$",
            message = "{error.form.field.password}")
    private String password;

    private Boolean admin = false;
    private Boolean active = true;
}
