package drk.shopamos.rest.mother;

import static drk.shopamos.rest.mother.AccountMother.buildAdminLuffy;
import static drk.shopamos.rest.mother.ProductMother.GMERRY_PROD_ID;
import static drk.shopamos.rest.mother.ProductMother.SHUSUI_PROD_ID;
import static drk.shopamos.rest.mother.ProductMother.buildGoingMerry;
import static drk.shopamos.rest.mother.ProductMother.buildShusui;
import static drk.shopamos.rest.mother.TimeMother.TODAY;
import static drk.shopamos.rest.mother.TimeMother.TOMORROW;

import drk.shopamos.rest.controller.request.OrderRequest;
import drk.shopamos.rest.controller.request.ProductQuantityRequest;
import drk.shopamos.rest.model.entity.Order;
import drk.shopamos.rest.model.entity.OrderProduct;
import drk.shopamos.rest.model.enumerable.OrderStatus;
import drk.shopamos.rest.service.model.ProductQuantity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderMother {
    public static Integer ORD_ID = 2;
    public static Integer SHUSUI_PROD_ORD_ID = 1;
    public static Integer SHUSUI_PROD_ORD_QUANTITY = 3;
    public static BigDecimal SHUSUI_PROD_ORD_UNITPRICE = BigDecimal.valueOf(3493.83);
    public static Integer GMERRY_PROD_ORD_ID = 2;
    public static Integer GMERRY_PROD_ORD_QUANTITY = 1;
    public static BigDecimal GMERRY_PROD_ORD_UNITPRICE = BigDecimal.valueOf(9998.99);
    public static LocalDateTime ORD_CREATED_DATE = TODAY;
    public static LocalDateTime ORD_UPDATED_DATE = TOMORROW;

    public static OrderStatus ORD_STATUS = OrderStatus.SUBMITTED;

    public static Order buildSwordsAndShipOrder() {
        return buildSwordsAndShipOrder(ORD_STATUS);
    }

    public static Order buildSwordsAndShipOrder(OrderStatus orderStatus) {
        Order order = new Order();
        order.setUser(buildAdminLuffy());
        order.setStatus(orderStatus);
        order.setCreatedDate(ORD_CREATED_DATE);
        order.setUpdatedDate(ORD_UPDATED_DATE);
        order.setOrderProducts(
                List.of(buildShusuiOrderProduct(order), buildGoingMerryOrderProduct(order)));
        return order;
    }

    public static List<ProductQuantity> buildSwordsAndShipQuantities() {
        ArrayList<ProductQuantity> productQuantities = new ArrayList<>();
        productQuantities.add(new ProductQuantity(SHUSUI_PROD_ORD_ID, 3));
        productQuantities.add(new ProductQuantity(GMERRY_PROD_ORD_ID, 1));
        return productQuantities;
    }

    public static OrderRequest buildSwordsAndShipOrderRequest() {
        return OrderRequest.builder()
                .productQuantities(
                        List.of(
                                ProductQuantityRequest.builder()
                                        .id(SHUSUI_PROD_ID)
                                        .quantity(SHUSUI_PROD_ORD_QUANTITY)
                                        .build(),
                                ProductQuantityRequest.builder()
                                        .id(GMERRY_PROD_ID)
                                        .quantity(GMERRY_PROD_ORD_QUANTITY)
                                        .build()))
                .build();
    }

    private static OrderProduct buildGoingMerryOrderProduct(Order order) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrder(order);
        orderProduct.setQuantity(GMERRY_PROD_ORD_QUANTITY);
        orderProduct.setUnitPrice(GMERRY_PROD_ORD_UNITPRICE);
        orderProduct.setProduct(buildGoingMerry());
        return orderProduct;
    }

    private static OrderProduct buildShusuiOrderProduct(Order order) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrder(order);
        orderProduct.setQuantity(SHUSUI_PROD_ORD_QUANTITY);
        orderProduct.setUnitPrice(SHUSUI_PROD_ORD_UNITPRICE);
        orderProduct.setProduct(buildShusui());
        return orderProduct;
    }
}
