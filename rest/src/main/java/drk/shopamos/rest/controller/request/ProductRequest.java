package drk.shopamos.rest.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    @NotEmpty(message = "{error.form.field.empty}")
    private String categoryId;

    @NotEmpty(message = "{error.form.field.empty}")
    @Size(max = 100, message = "{error.form.field.maxlength}")
    private String name;

    private String description;

    @NotNull(message = "{error.form.field.empty}")
    @Min(value = 0, message = "{error.form.field.positive}")
    private BigDecimal price;

    private String imageUrl;

    private boolean isActive;
}
