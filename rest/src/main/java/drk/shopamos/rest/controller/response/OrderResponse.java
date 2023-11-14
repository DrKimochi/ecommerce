package drk.shopamos.rest.controller.response;

import drk.shopamos.rest.model.enumerable.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private String customerEmail;
    private OrderStatus status;
    private List<OrderItemResponse> orderItems;
    private BigDecimal orderTotal;
}
