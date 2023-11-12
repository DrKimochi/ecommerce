package drk.shopamos.rest.service;

import static drk.shopamos.rest.config.MessageProvider.MSG_ORDER_STATUS_REQUIRED;
import static drk.shopamos.rest.mother.AccountMother.LUFFY_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.LUFFY_ID;
import static drk.shopamos.rest.mother.AccountMother.buildAdminLuffy;
import static drk.shopamos.rest.mother.MessageMother.MSG_NOT_FOUND_ID;
import static drk.shopamos.rest.mother.MessageMother.MSG_ORDER_ITEM_QTY_GT0;
import static drk.shopamos.rest.mother.MessageMother.MSG_ORDER_NO_ITEMS;
import static drk.shopamos.rest.mother.MessageMother.MSG_USERNAME_REQUIRED;
import static drk.shopamos.rest.mother.OrderMother.GMERRY_PROD_ORD_ID;
import static drk.shopamos.rest.mother.OrderMother.ORD_CREATED_DATE;
import static drk.shopamos.rest.mother.OrderMother.ORD_ID;
import static drk.shopamos.rest.mother.OrderMother.ORD_STATUS;
import static drk.shopamos.rest.mother.OrderMother.ORD_UPDATED_DATE;
import static drk.shopamos.rest.mother.OrderMother.SHUSUI_PROD_ORD_ID;
import static drk.shopamos.rest.mother.OrderMother.buildSwordsAndShipOrder;
import static drk.shopamos.rest.mother.OrderMother.buildSwordsAndShipQuantities;
import static drk.shopamos.rest.mother.ProductMother.buildGoingMerry;
import static drk.shopamos.rest.mother.ProductMother.buildShusui;
import static drk.shopamos.rest.mother.TimeMother.TODAY_INSTANT;
import static drk.shopamos.rest.mother.TimeMother.TOMORROW_INSTANT;
import static drk.shopamos.rest.mother.TimeMother.ZONE_ID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import drk.shopamos.rest.model.entity.Order;
import drk.shopamos.rest.model.enumerable.OrderStatus;
import drk.shopamos.rest.repository.AccountRepository;
import drk.shopamos.rest.repository.OrderRepository;
import drk.shopamos.rest.repository.ProductRepository;
import drk.shopamos.rest.service.exception.BadDataException;
import drk.shopamos.rest.service.exception.EntityNotFoundException;
import drk.shopamos.rest.service.model.ProductQuantity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest extends ServiceTest {
    @Mock AccountRepository accountRepository;
    @Mock ProductRepository productRepository;
    @Mock OrderRepository orderRepository;
    @Mock Clock clock;
    @InjectMocks OrderService testee;

    @Test
    @DisplayName("createOrder - when Null user then exception is thrown")
    void createOrder_whenNullUser_throwException() {
        assertException(
                BadDataException.class,
                () -> testee.createOrder(null, buildSwordsAndShipQuantities()),
                MSG_USERNAME_REQUIRED);
    }

    @Test
    @DisplayName("createOrder - when User does not exist then exception is thrown")
    void createOrder_whenUserDoesNotExist_throwException() {
        assertException(
                EntityNotFoundException.class,
                () -> testee.createOrder(buildAdminLuffy(), buildSwordsAndShipQuantities()),
                MSG_NOT_FOUND_ID,
                LUFFY_ID);
    }

    @Test
    @DisplayName("createOrder - when Null productQuantities then exception is thrown")
    void createOrder_whenNullProductQuantities_throwException() {
        when(accountRepository.existsById(LUFFY_ID)).thenReturn(true);
        assertException(
                BadDataException.class,
                () -> testee.createOrder(buildAdminLuffy(), null),
                MSG_ORDER_NO_ITEMS);
    }

    @Test
    @DisplayName("createOrder - when empty productQuantities then exception is thrown")
    void createOrder_whenEmptyProductQuantities_throwException() {
        when(accountRepository.existsById(LUFFY_ID)).thenReturn(true);
        assertException(
                BadDataException.class,
                () -> testee.createOrder(buildAdminLuffy(), new ArrayList<>()),
                MSG_ORDER_NO_ITEMS);
    }

    @Test
    @DisplayName(
            "createOrder - when productQuantities contains null element then exception is thrown")
    void createOrder_whenProductQuantities_containsNullElement_throwException() {
        List<ProductQuantity> productQuantities = buildSwordsAndShipQuantities();
        productQuantities.add(null);
        when(accountRepository.existsById(LUFFY_ID)).thenReturn(true);
        assertException(
                BadDataException.class,
                () -> testee.createOrder(buildAdminLuffy(), productQuantities),
                MSG_ORDER_ITEM_QTY_GT0);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName(
            "createOrder - when productQuantities contains element with quantity less than 1 then exception is thrown")
    void createOrder_whenProductQuantities_containsNullElement_throwException(Integer quantity) {
        List<ProductQuantity> productQuantities = buildSwordsAndShipQuantities();
        productQuantities.get(0).setQuantity(quantity);
        when(accountRepository.existsById(LUFFY_ID)).thenReturn(true);
        assertException(
                BadDataException.class,
                () -> testee.createOrder(buildAdminLuffy(), productQuantities),
                MSG_ORDER_ITEM_QTY_GT0);
    }

    @Test
    @DisplayName("createOrder - when params are valid, then builds order and saves it")
    void createOrder_whenValidParams_thenBuildOrder_andSave() {
        Order expectedOrder = buildSwordsAndShipOrder();
        Order savedOrder = buildSwordsAndShipOrder();
        when(accountRepository.existsById(LUFFY_ID)).thenReturn(true);
        when(productRepository.findById(SHUSUI_PROD_ORD_ID)).thenReturn(Optional.of(buildShusui()));
        when(productRepository.findById(GMERRY_PROD_ORD_ID))
                .thenReturn(Optional.of(buildGoingMerry()));
        when(clock.instant()).thenReturn(TODAY_INSTANT, TOMORROW_INSTANT);
        when(clock.getZone()).thenReturn(ZONE_ID);
        when(orderRepository.save(expectedOrder)).thenReturn(savedOrder);

        Order resultOrder = testee.createOrder(buildAdminLuffy(), buildSwordsAndShipQuantities());
        assertThat(resultOrder, is(savedOrder));
    }

    @Test
    @DisplayName("updateOrder - when null order status then throws exception")
    void updateOrder_whenNullOrderStatus_throwsException() {
        assertException(
                BadDataException.class,
                () -> testee.updateOrder(ORD_ID, null),
                MSG_ORDER_STATUS_REQUIRED);
    }

    @Test
    @DisplayName("updateOrder - when order does not exist then throws exception")
    void updateOrder_whenOrderDoesNotExist_throwsException() {
        when(orderRepository.findById(ORD_ID)).thenReturn(Optional.empty());
        assertException(
                EntityNotFoundException.class,
                () -> testee.updateOrder(ORD_ID, OrderStatus.CONFIRMED),
                MSG_NOT_FOUND_ID,
                ORD_ID);
    }

    @Test
    @DisplayName("updateOrder - when valid params then saves new status")
    void updateOrder_whenValidParams_savesNewStatus() {
        Order originalOrder = buildSwordsAndShipOrder();
        Order modifiedOrder = buildSwordsAndShipOrder(OrderStatus.CONFIRMED);
        Order savedOrder = buildSwordsAndShipOrder(OrderStatus.CONFIRMED);
        when(orderRepository.findById(ORD_ID)).thenReturn(Optional.of(originalOrder));
        when(orderRepository.save(modifiedOrder)).thenReturn(savedOrder);

        Order returnedOrder = testee.updateOrder(ORD_ID, OrderStatus.CONFIRMED);
        assertThat(returnedOrder, is(savedOrder));
    }

    @Test
    @DisplayName("deleteOrder -  when order does not exist then throws exception")
    void deleteOrder_whenOrderIdDoesNotExist_throwsException() {
        when(orderRepository.findById(ORD_ID)).thenReturn(Optional.empty());
        assertException(
                EntityNotFoundException.class,
                () -> testee.deleteOrder(ORD_ID),
                MSG_NOT_FOUND_ID,
                ORD_ID);
    }

    @Test
    @DisplayName("deleteOrder - when ID exists then deletes order")
    void deleteOrder_whenOrderIdExists_deletesOrder() {
        Order existingOrder = buildSwordsAndShipOrder();
        when(orderRepository.findById(ORD_ID)).thenReturn(Optional.of(existingOrder));
        testee.deleteOrder(ORD_ID);
        verify(orderRepository).delete(existingOrder);
    }

    @Test
    @DisplayName("getOrder - when order does not exist then throws exception")
    void getOrder_whenOrderIdDoesNotExist_throwsException() {
        when(orderRepository.findById(ORD_ID)).thenReturn(Optional.empty());
        assertException(
                EntityNotFoundException.class,
                () -> testee.getOrder(ORD_ID),
                MSG_NOT_FOUND_ID,
                ORD_ID);
    }

    @Test
    @DisplayName("getOrder - when ID exists then returns order")
    void getOrder_whenOrderIdExists_returnsOrder() {
        Order existingOrder = buildSwordsAndShipOrder();
        when(orderRepository.findById(ORD_ID)).thenReturn(Optional.of(existingOrder));
        Order returnedOrder = testee.getOrder(ORD_ID);
        assertThat(returnedOrder, is(existingOrder));
    }

    @Test
    @DisplayName("searchOrders -  when all params populated finds all orders by attributes")
    void searchOrders_whenParamsPopulated_findsAllOrdersByAttributes() {
        List<Order> foundOrders = List.of(buildSwordsAndShipOrder());
        when(orderRepository.findAllByAttributes(
                        LUFFY_EMAIL,
                        ORD_STATUS,
                        ORD_CREATED_DATE,
                        ORD_UPDATED_DATE,
                        List.of(GMERRY_PROD_ORD_ID),
                        1))
                .thenReturn(foundOrders);

        List<Order> returnedOrders =
                testee.searchOrders(
                        LUFFY_EMAIL,
                        ORD_STATUS,
                        ORD_CREATED_DATE,
                        ORD_UPDATED_DATE,
                        List.of(GMERRY_PROD_ORD_ID));

        assertThat(returnedOrders, is(returnedOrders));
    }

    @Test
    @DisplayName("searchOrders -  when all params are null finds all orders by attributes")
    void searchOrders_whenParamsNull_findsAllOrdersByAttributes() {
        List<Order> foundOrders = List.of(buildSwordsAndShipOrder());
        when(orderRepository.findAllByAttributes(null, null, null, null, new ArrayList<>(), 0))
                .thenReturn(foundOrders);

        List<Order> returnedOrders = testee.searchOrders(null, null, null, null, null);

        assertThat(returnedOrders, is(returnedOrders));
    }
}
