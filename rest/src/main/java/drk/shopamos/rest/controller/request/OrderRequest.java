package drk.shopamos.rest.controller.request;

import drk.shopamos.rest.controller.group.Create;
import drk.shopamos.rest.controller.group.Update;
import drk.shopamos.rest.model.enumerable.OrderStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    @NotNull(
            groups = {Update.class},
            message = "{error.form.field.empty}")
    private OrderStatus status;

    @NotEmpty(
            groups = {Create.class},
            message = "{error.form.field.empty}")
    private List<@Valid ProductQuantityRequest> productQuantities;
}
