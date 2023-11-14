package drk.shopamos.rest.controller.request;

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
    @NotNull(message = "{error.form.field.empty}")
    private Integer id;
    @NotNull(message = "{error.form.field.empty}")
    @Min(value = 1, message = "{error.form.field.positive}")
    private Integer quantity;
}
