package drk.shopamos.rest.controller;

import static drk.shopamos.rest.mother.AccountMother.LUFFY_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.LUFFY_ID;
import static drk.shopamos.rest.mother.OrderMother.GMERRY_PROD_ORD_QUANTITY;
import static drk.shopamos.rest.mother.OrderMother.GMERRY_PROD_ORD_UNITPRICE;
import static drk.shopamos.rest.mother.OrderMother.ORD_ID;
import static drk.shopamos.rest.mother.OrderMother.SHUSUI_PROD_ORD_QUANTITY;
import static drk.shopamos.rest.mother.OrderMother.SHUSUI_PROD_ORD_UNITPRICE;
import static drk.shopamos.rest.mother.OrderMother.buildSwordsAndShipOrder;
import static drk.shopamos.rest.mother.OrderMother.buildSwordsAndShipOrderRequest;
import static drk.shopamos.rest.mother.OrderMother.buildSwordsAndShipQuantities;
import static drk.shopamos.rest.mother.ProductMother.GMERRY_PROD_ID;
import static drk.shopamos.rest.mother.ProductMother.GMERRY_PROD_NAME;
import static drk.shopamos.rest.mother.ProductMother.SHUSUI_PROD_ID;
import static drk.shopamos.rest.mother.ProductMother.SHUSUI_PROD_NAME;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

import drk.shopamos.rest.controller.mapper.OrderMapperImpl;
import drk.shopamos.rest.controller.request.OrderRequest;
import drk.shopamos.rest.controller.response.ErrorResponse;
import drk.shopamos.rest.controller.response.OrderItemResponse;
import drk.shopamos.rest.controller.response.OrderResponse;
import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.model.entity.Order;
import drk.shopamos.rest.model.enumerable.OrderStatus;
import drk.shopamos.rest.service.OrderService;
import drk.shopamos.rest.service.model.ProductQuantity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@WebMvcTest
@ContextConfiguration(classes = {OrderController.class, OrderMapperImpl.class})
class OrderControllerTest extends ControllerTest {
    private static final String ORDER_URI = "/v1/orders";
    private static final String ORDER_URI_WITH_ID = "/v1/orders/{id}";
    @MockBean protected OrderService orderService;
    @Captor ArgumentCaptor<Account> accountCaptor;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName(
            "createOrder - when valid body for authenticated user then calls service createOrder and returns an OrderResponse")
    void createOrder_whenValidRequest_returnsOrderResponse(Boolean isAdmin) throws Exception {
        final OrderRequest orderRequest = buildSwordsAndShipOrderRequest();
        final List<ProductQuantity> productQuantities = buildSwordsAndShipQuantities();
        final Order order = buildSwordsAndShipOrder();

        when(orderService.createOrder(any(Account.class), eq(productQuantities))).thenReturn(order);

        OrderResponse orderResponse =
                getMvc().send(POST, ORDER_URI)
                        .withBody(orderRequest)
                        .withJwt(token(LUFFY_ID, isAdmin))
                        .thenExpectStatus(OK)
                        .getResponseBody(OrderResponse.class);

        verify(orderService).createOrder(accountCaptor.capture(), any());
        assertThat(accountCaptor.getValue().getId(), is(LUFFY_ID));
        assertOrderResponse(orderResponse);
    }

    @Test
    @DisplayName("createOrder - when user not authenticated, return error response")
    void createOrder_whenNullProductQuantities_returns403Response() throws Exception {
        final OrderRequest orderRequest = buildSwordsAndShipOrderRequest();
        getMvc().send(POST, ORDER_URI).withBody(orderRequest).thenExpectStatus(FORBIDDEN);
    }

    @Test
    @DisplayName("createOrder - when productQuantities are not in the body, return error response")
    void createOrder_whenNullProductQuantities_returns400Response() throws Exception {
        final OrderRequest orderRequest = new OrderRequest();

        ErrorResponse errorResponse =
                getMvc().send(POST, ORDER_URI)
                        .withBody(orderRequest)
                        .withJwt(adminToken(LUFFY_ID))
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);

        errorResponseAssert.emptyField(errorResponse, "productQuantities");
    }

    @Test
    @DisplayName(
            "createOrder - when a productQuantity does not have a productId, return error response")
    void createOrder_whenNullProductId_returns400Response() throws Exception {
        OrderRequest orderRequest = buildSwordsAndShipOrderRequest();
        orderRequest.getProductQuantities().get(0).setId(null);

        ErrorResponse errorResponse =
                getMvc().send(POST, ORDER_URI)
                        .withBody(orderRequest)
                        .withJwt(adminToken(LUFFY_ID))
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);

        errorResponseAssert.emptyField(errorResponse, "productQuantities[0].id");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {-1, 0})
    @DisplayName("createOrder - when a quantity not greater than 1, return error response")
    void createOrder_whenQuantityNotGraterThan1_returns400Response(Integer quantity)
            throws Exception {
        OrderRequest orderRequest = buildSwordsAndShipOrderRequest();
        orderRequest.getProductQuantities().get(0).setQuantity(quantity);

        ErrorResponse errorResponse =
                getMvc().send(POST, ORDER_URI)
                        .withBody(orderRequest)
                        .withJwt(adminToken(LUFFY_ID))
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);

        errorResponseAssert.positiveField(errorResponse, "productQuantities[0].quantity");
    }

    @Test
    @DisplayName(
            "updateOrder - when valid body for admin user then calls service and returns an OrderResponse")
    void updateOrder_whenValidRequest_returnsOrderResponse() throws Exception {
        final Order order = buildSwordsAndShipOrder();
        final OrderRequest orderRequest =
                OrderRequest.builder().status(OrderStatus.CONFIRMED).build();

        when(orderService.updateOrder(ORD_ID, OrderStatus.CONFIRMED)).thenReturn(order);

        OrderResponse orderResponse =
                getMvc().send(PUT, ORDER_URI_WITH_ID, ORD_ID)
                        .withBody(orderRequest)
                        .withJwt(adminToken(LUFFY_ID))
                        .thenExpectStatus(OK)
                        .getResponseBody(OrderResponse.class);

        assertOrderResponse(orderResponse);
    }

    @Test
    @DisplayName("updateOrder - when user not authenticated, return forbidden response")
    void updateOrder_whenUserNotAuthenticated_returnsForbiddenResponse() throws Exception {
        final OrderRequest orderRequest = buildSwordsAndShipOrderRequest();

        getMvc().send(PUT, ORDER_URI_WITH_ID, ORD_ID)
                .withBody(orderRequest)
                .thenExpectStatus(FORBIDDEN);
    }

    @Test
    @DisplayName("updateOrder - when user is customer, return forbidden response")
    void updateOrder_whenUserIsCustomer_returnsForbiddenResponse() throws Exception {
        final OrderRequest orderRequest = buildSwordsAndShipOrderRequest(OrderStatus.CONFIRMED);
        getMvc().send(PUT, ORDER_URI_WITH_ID, ORD_ID)
                .withBody(orderRequest)
                .withJwt(customerToken(LUFFY_ID))
                .thenExpectStatus(FORBIDDEN);
    }

    @Test
    @DisplayName(
            "updateOrder - when order status is not a valid enum return 400 response with message ")
    void updateOrder_whenOrderStatusNotValidEnum_returns400Response() throws Exception {

        ErrorResponse errorResponse =
                getMvc().send(PUT, ORDER_URI_WITH_ID, ORD_ID)
                        .withBody(Map.of("status", "LOST"))
                        .withJwt(adminToken(LUFFY_ID))
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);

        errorResponseAssert.enumField(errorResponse, "LOST", OrderStatus.class);
    }

    @Test
    @DisplayName("deleteOrder - when user is admin then call service layer and return 200")
    void deleteOrder_whenUserIsAdmin_return200Response() throws Exception {
        getMvc().send(DELETE, ORDER_URI_WITH_ID, ORD_ID)
                .withJwt(adminToken(LUFFY_ID))
                .thenExpectStatus(OK);

        verify(orderService).deleteOrder(ORD_ID);
    }

    @Test
    @DisplayName("deleteOrder - when user not authenticated, return 503 response")
    void deleteOrder_whenUserNotAuthenticated_return503Response() throws Exception {
        getMvc().send(DELETE, ORDER_URI_WITH_ID, ORD_ID).thenExpectStatus(FORBIDDEN);
    }

    @Test
    @DisplayName("deleteOrder - when user is customer, return 503 response")
    void deleteOrder_whenUserIsCustomer_return503Response() throws Exception {
        getMvc().send(DELETE, ORDER_URI_WITH_ID, ORD_ID)
                .withJwt(customerToken(LUFFY_ID))
                .thenExpectStatus(FORBIDDEN);
    }

    @Test
    @DisplayName("getOrder - when user is customer , return 503 response")
    void getOrder_whenUserIsCustomer_return503Response() throws Exception {
        getMvc().send(GET, ORDER_URI_WITH_ID, ORD_ID)
                .withJwt(customerToken(LUFFY_ID))
                .thenExpectStatus(FORBIDDEN);
    }

    @Test
    @DisplayName("getOrder - when user is not authenticated , return 503 response")
    void getOrder_whenUserNotAuthenticated_return503Response() throws Exception {
        getMvc().send(GET, ORDER_URI_WITH_ID, ORD_ID).thenExpectStatus(FORBIDDEN);
    }

    @Test
    @DisplayName("getOrder - when user is admin , call service layer and return 503")
    void getOrder_whenUserIsAdmin_return200Response() throws Exception {
        Order order = buildSwordsAndShipOrder();
        when(orderService.getOrder(ORD_ID)).thenReturn(order);
        OrderResponse orderResponse =
                getMvc().send(GET, ORDER_URI_WITH_ID, ORD_ID)
                        .withJwt(adminToken(LUFFY_ID))
                        .thenExpectStatus(OK)
                        .getResponseBody(OrderResponse.class);

        assertOrderResponse(orderResponse);
    }

    private void assertOrderResponse(OrderResponse orderResponse) {
        assertThat(orderResponse.getCustomerEmail(), is(LUFFY_EMAIL));
        assertThat(orderResponse.getStatus(), is(OrderStatus.SUBMITTED));
        assertThat(orderResponse.getOrderTotal(), is(BigDecimal.valueOf(20480.48)));
        List<OrderItemResponse> orderItems = orderResponse.getOrderItems();
        assertThat(orderItems.get(0).getProductId(), is(SHUSUI_PROD_ID));
        assertThat(orderItems.get(0).getProductName(), is(SHUSUI_PROD_NAME));
        assertThat(orderItems.get(0).getQuantity(), is(SHUSUI_PROD_ORD_QUANTITY));
        assertThat(orderItems.get(0).getUnitPrice(), is(SHUSUI_PROD_ORD_UNITPRICE));
        assertThat(orderItems.get(0).getTotal(), is(BigDecimal.valueOf(10481.49)));
        assertThat(orderItems.get(1).getProductId(), is(GMERRY_PROD_ID));
        assertThat(orderItems.get(1).getProductName(), is(GMERRY_PROD_NAME));
        assertThat(orderItems.get(1).getQuantity(), is(GMERRY_PROD_ORD_QUANTITY));
        assertThat(orderItems.get(1).getUnitPrice(), is(GMERRY_PROD_ORD_UNITPRICE));
        assertThat(orderItems.get(1).getTotal(), is(BigDecimal.valueOf(9998.99)));
    }
}
