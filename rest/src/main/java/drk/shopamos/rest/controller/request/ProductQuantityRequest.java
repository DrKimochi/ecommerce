package drk.shopamos.rest.controller.request;

import drk.shopamos.rest.controller.group.Create;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductQuantityRequest {
    @NotNull(
            message = "{error.form.field.empty}",
            groups = {Create.class})
    private Integer id;

    @NotNull(
            message = "{error.form.field.positive}",
            groups = {Create.class})
    @Min(
            value = 1,
            message = "{error.form.field.positive}",
            groups = {Create.class})
    private Integer quantity;
}
