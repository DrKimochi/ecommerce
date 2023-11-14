package drk.shopamos.rest.controller.mapper;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

import drk.shopamos.rest.controller.request.ProductQuantityRequest;
import drk.shopamos.rest.controller.response.OrderItemResponse;
import drk.shopamos.rest.controller.response.OrderResponse;
import drk.shopamos.rest.model.entity.Order;
import drk.shopamos.rest.model.entity.OrderProduct;
import drk.shopamos.rest.service.model.ProductQuantity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface OrderMapper {

    List<ProductQuantity> map(List<ProductQuantityRequest> productQuantityRequests);

    @Mapping(target = "status", source = "order.status")
    @Mapping(target = "customerEmail", source = "order.user.email")
    @Mapping(target = "orderTotal", source = "order", qualifiedByName = "getOrderTotal")
    @Mapping(target = "orderItems", source = "order", qualifiedByName = "getOrderItems")
    OrderResponse map(Order order);

    @Named("getOrderTotal")
    default BigDecimal getOrderTotal(Order order) {
        return emptyIfNull(order.getOrderProducts()).stream()
                .map(this::getOrderItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Named("getOrderItems")
    default List<OrderItemResponse> getOrderItems(Order order) {
        return emptyIfNull(order.getOrderProducts()).stream().map(this::mapOrderItem).toList();
    }

    @Mapping(target = "productId", source = "orderProduct.product.id")
    @Mapping(target = "productName", source = "orderProduct.product.name")
    @Mapping(target = "quantity", source = "orderProduct.quantity")
    @Mapping(target = "unitPrice", source = "orderProduct.unitPrice")
    @Mapping(target = "total", source = "orderProduct", qualifiedByName = "getOrderItemTotal")
    OrderItemResponse mapOrderItem(OrderProduct orderProduct);

    @Named("getOrderItemTotal")
    default BigDecimal getOrderItemTotal(OrderProduct orderProduct) {
        BigDecimal unitPrice = orderProduct.getUnitPrice();
        BigDecimal quantity = BigDecimal.valueOf(orderProduct.getQuantity());
        return unitPrice.multiply(quantity);
    }
}
