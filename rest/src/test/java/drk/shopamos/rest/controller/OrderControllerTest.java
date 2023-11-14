package drk.shopamos.rest.controller;

import static drk.shopamos.rest.mother.AccountMother.LUFFY_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.LUFFY_ID;
import static drk.shopamos.rest.mother.OrderMother.GMERRY_PROD_ORD_QUANTITY;
import static drk.shopamos.rest.mother.OrderMother.GMERRY_PROD_ORD_UNITPRICE;
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
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

import drk.shopamos.rest.controller.mapper.OrderMapperImpl;
import drk.shopamos.rest.controller.response.OrderItemResponse;
import drk.shopamos.rest.controller.response.OrderResponse;
import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.model.enumerable.OrderStatus;
import drk.shopamos.rest.service.OrderService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.List;

@WebMvcTest
@ContextConfiguration(classes = {OrderController.class, OrderMapperImpl.class})
class OrderControllerTest extends ControllerTest {
    private static final String ORDER_URI = "/v1/orders";
    @MockBean protected OrderService orderService;

    @Captor ArgumentCaptor<Account> accountCaptor;

    @Test
    @DisplayName(
            "createOrder - when valid body then calls service createOrder and returns an OrderResponse")
    void createOrder_whenValidRequest_returnsOrderResponse() throws Exception {
        when(orderService.createOrder(any(Account.class), eq(buildSwordsAndShipQuantities())))
                .thenReturn(buildSwordsAndShipOrder());
        OrderResponse orderResponse =
                getMvc().send(POST, ORDER_URI)
                        .withBody(buildSwordsAndShipOrderRequest())
                        .withJwt(adminToken(LUFFY_ID))
                        .thenExpectStatus(OK)
                        .getResponseBody(OrderResponse.class);
        verify(orderService).createOrder(accountCaptor.capture(), any());
        assertThat(accountCaptor.getValue().getId(), is(LUFFY_ID));
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
