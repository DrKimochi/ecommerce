package drk.shopamos.rest.mother;

import static drk.shopamos.rest.mother.AccountMother.buildAdminLuffy;
import static drk.shopamos.rest.mother.ProductMother.buildGoingMerry;
import static drk.shopamos.rest.mother.ProductMother.buildShusui;
import static drk.shopamos.rest.mother.TimeMother.TODAY;
import static drk.shopamos.rest.mother.TimeMother.TOMORROW;

import drk.shopamos.rest.model.entity.Order;
import drk.shopamos.rest.model.entity.OrderProduct;
import drk.shopamos.rest.model.enumerable.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderMother {
    public static Integer ORD_ID = 2;
    public static Integer SHUSUI_PROD_ORD_ID = 1;
    public static Integer SHUSUI_PROD_ORD_QUANTITY = 3;
    public static BigDecimal SHUSUI_PROD_ORD_UNITPRICE = BigDecimal.valueOf(420.69);
    public static Integer GMERRY_PROD_ORD_ID = 2;
    public static Integer GMERRY_PROD_ORD_QUANTITY = 1;
    public static BigDecimal GMERRY_PROD_ORD_UNITPRICE = BigDecimal.valueOf(1337.42);
    public static LocalDateTime ORD_CREATED_DATE = TODAY;
    public static LocalDateTime ORD_UPDATED_DATE = TOMORROW;

    public static OrderStatus ORD_STATUS = OrderStatus.SUBMITTED;

    public static Order buildOrderWithTwoItems() {
        Order order = new Order();
        order.setId(ORD_ID);
        order.setUser(buildAdminLuffy());
        order.setStatus(ORD_STATUS);
        order.setCreatedDate(ORD_CREATED_DATE);
        order.setUpdatedDate(ORD_UPDATED_DATE);
        order.setOrderProducts(List.of(buildGoingMerryOrderProduct(), buildShusuiOrderProduct()));
        return order;
    }

    public static OrderProduct buildGoingMerryOrderProduct() {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(GMERRY_PROD_ORD_ID);
        orderProduct.setQuantity(GMERRY_PROD_ORD_QUANTITY);
        orderProduct.setUnitPrice(GMERRY_PROD_ORD_UNITPRICE);
        orderProduct.setProduct(buildGoingMerry());
        return orderProduct;
    }

    public static OrderProduct buildShusuiOrderProduct() {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(SHUSUI_PROD_ORD_ID);
        orderProduct.setQuantity(SHUSUI_PROD_ORD_QUANTITY);
        orderProduct.setUnitPrice(SHUSUI_PROD_ORD_UNITPRICE);
        orderProduct.setProduct(buildShusui());
        return orderProduct;
    }
}
