package drk.shopamos.rest.service;

import static drk.shopamos.rest.config.MessageProvider.MSG_ORDER_ITEM_QTY_GT0;
import static drk.shopamos.rest.config.MessageProvider.MSG_ORDER_NO_ITEMS;
import static drk.shopamos.rest.config.MessageProvider.MSG_USERNAME_REQUIRED;

import static java.util.Objects.isNull;

import drk.shopamos.rest.config.MessageProvider;
import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.model.entity.Order;
import drk.shopamos.rest.model.entity.OrderProduct;
import drk.shopamos.rest.model.entity.Product;
import drk.shopamos.rest.model.enumerable.OrderStatus;
import drk.shopamos.rest.repository.AccountRepository;
import drk.shopamos.rest.repository.OrderRepository;
import drk.shopamos.rest.repository.ProductRepository;
import drk.shopamos.rest.service.model.ProductQuantity;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService extends BaseService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            AccountRepository accountRepository,
            MessageProvider messageProvider,
            Clock clock) {
        super(messageProvider, clock);
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.accountRepository = accountRepository;
    }

    public Order createOrder(Account user, List<ProductQuantity> productQuantities) {
        validateParams(user, productQuantities);
        Order order =
                Order.builder()
                        .user(user)
                        .status(OrderStatus.SUBMITTED)
                        .orderProducts(createOrderProducts(productQuantities))
                        .createdDate(now())
                        .updatedDate(now())
                        .build();

        return orderRepository.save(order);
    }

    public Order updateOrder(Integer orderId, OrderStatus newStatus) {
        Order order = getOrder(orderId);
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    public void deleteOrder(Integer orderId) {
        Order order = getOrder(orderId);
        orderRepository.delete(order);
    }

    public Order getOrder(Integer orderId) {
        return orderRepository.findById(orderId).orElseThrow(anEntityNotFoundException(orderId));
    }

    public List<Order> searchOrders(
            String username,
            OrderStatus orderStatus,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            List<Integer> productIds) {

        List<Integer> nonNullProductIds = productIds != null ? productIds : new ArrayList<>();
        return orderRepository.findAllByAttributes(
                username,
                orderStatus,
                dateFrom,
                dateTo,
                nonNullProductIds,
                nonNullProductIds.size());
    }

    private List<OrderProduct> createOrderProducts(List<ProductQuantity> productQuantities) {
        return productQuantities.stream().map(this::createOrderProduct).toList();
    }

    private OrderProduct createOrderProduct(ProductQuantity productQuantity) {
        Product product = getProduct(productQuantity.getId());
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProduct.setUnitPrice(product.getPrice());
        orderProduct.setQuantity(productQuantity.getQuantity());
        return orderProduct;
    }

    private Product getProduct(Integer productId) {
        return productRepository
                .findById(productId)
                .orElseThrow(anEntityNotFoundException(productId));
    }

    private void validateParams(Account user, List<ProductQuantity> productQuantities) {
        validateUser(user);
        validateProductQuantities(productQuantities);
    }

    private void validateUser(Account user) {
        if (isNull(user)) {
            throw aBadDataException(MSG_USERNAME_REQUIRED).get();
        }
        if (!accountRepository.existsById(user.getId())) {
            throw anEntityNotFoundException(user.getId()).get();
        }
    }

    private void validateProductQuantities(List<ProductQuantity> productQuantities) {

        if (isNull(productQuantities) || productQuantities.isEmpty()) {
            throw aBadDataException(MSG_ORDER_NO_ITEMS).get();
        }
        productQuantities.forEach(
                productQuantity -> {
                    if (isNull(productQuantity) || productQuantity.getQuantity() < 1) {
                        throw aBadDataException(MSG_ORDER_ITEM_QTY_GT0).get();
                    }
                });
    }
}
