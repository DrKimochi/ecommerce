package drk.shopamos.rest.controller.request;

import drk.shopamos.rest.controller.group.Create;
import drk.shopamos.rest.controller.group.Update;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {

    @NotEmpty(
            groups = {Create.class},
            message = "{error.form.field.empty}")
    @Pattern(
            groups = {Create.class},
            regexp = "^[A-Z]{4}$",
            message = "{error.form.field.category}")
    private String id;

    @NotEmpty(
            groups = {Create.class, Update.class},
            message = "{error.form.field.empty}")
    private String name;

    private String description;

    private String iconUrl;
}
