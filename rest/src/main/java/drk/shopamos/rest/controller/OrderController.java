package drk.shopamos.rest.controller;

import drk.shopamos.rest.controller.group.Create;
import drk.shopamos.rest.controller.group.Update;
import drk.shopamos.rest.controller.mapper.OrderMapper;
import drk.shopamos.rest.controller.request.OrderRequest;
import drk.shopamos.rest.controller.response.OrderResponse;
import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.model.entity.Order;
import drk.shopamos.rest.model.enumerable.OrderStatus;
import drk.shopamos.rest.service.OrderService;
import drk.shopamos.rest.service.model.ProductQuantity;

import jakarta.validation.constraints.NotEmpty;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
public class OrderController extends BaseController {
    private final OrderService service;
    private final OrderMapper mapper;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Validated(Create.class) @RequestBody @NotEmpty OrderRequest orderRequest) {
        Account authUser = getPrincipal();
        List<ProductQuantity> productQuantities = mapper.map(orderRequest.getProductQuantities());

        Order order = service.createOrder(authUser, productQuantities);
        OrderResponse response = mapper.map(order);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable(name = "id") Integer id,
            @Validated(Update.class) @RequestBody @NotEmpty OrderRequest orderRequest) {
        Order order = service.updateOrder(id, orderRequest.getStatus());
        OrderResponse response = mapper.map(order);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable(name = "id") Integer id) {
        service.deleteOrder(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable(name = "id") Integer id) {
        Order order = service.getOrder(id);
        OrderResponse response = mapper.map(order);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getOrders(
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "orderStatus", required = false) OrderStatus orderStatus,
            @RequestParam(name = "dateFrom", required = false)
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDateTime dateFrom,
            @RequestParam(name = "dateFrom", required = false)
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDateTime dateTo,
            @RequestParam(name = "productId", required = false)
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    List<Integer> productIds) {

        List<Order> orders =
                service.searchOrders(username, orderStatus, dateFrom, dateTo, productIds);
        List<OrderResponse> response = orders.stream().map(mapper::map).toList();
        return ResponseEntity.ok(response);
    }
}
