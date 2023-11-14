package drk.shopamos.rest.controller;

import drk.shopamos.rest.controller.group.Create;
import drk.shopamos.rest.controller.mapper.OrderMapper;
import drk.shopamos.rest.controller.request.OrderRequest;
import drk.shopamos.rest.controller.response.OrderResponse;
import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.model.entity.Order;
import drk.shopamos.rest.service.OrderService;
import drk.shopamos.rest.service.model.ProductQuantity;

import jakarta.validation.constraints.NotEmpty;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
public class OrderController extends BaseController {

    private final OrderService service;
    private final OrderMapper mapper;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ADMIN')")
    public ResponseEntity<OrderResponse> createOrder(
            @Validated(Create.class) @RequestBody @NotEmpty OrderRequest orderRequest) {
        Account authUser = getPrincipal();
        List<ProductQuantity> productQuantities = mapper.map(orderRequest.getProductQuantities());

        Order order = service.createOrder(authUser, productQuantities);
        OrderResponse response = mapper.map(order);

        return ResponseEntity.ok(response);
    }
}
