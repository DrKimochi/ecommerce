package drk.shopamos.rest.repository;

import static drk.shopamos.rest.mother.AccountMother.LUFFY_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.LUFFY_ID;
import static drk.shopamos.rest.mother.AccountMother.LUFFY_NAME;
import static drk.shopamos.rest.mother.OrderMother.ORD_CREATED_DATE;
import static drk.shopamos.rest.mother.OrderMother.ORD_STATUS;
import static drk.shopamos.rest.mother.OrderMother.ORD_UPDATED_DATE;
import static drk.shopamos.rest.mother.OrderMother.buildNewOrderWithTwoItems;
import static drk.shopamos.rest.mother.ProductMother.GMERRY_PROD_NAME;
import static drk.shopamos.rest.mother.ProductMother.SHUSUI_PROD_NAME;
import static drk.shopamos.rest.mother.TimeMother.DECEMBER_2ND_1PM;
import static drk.shopamos.rest.mother.TimeMother.NOVEMBER_4TH_3PM;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.model.entity.Order;
import drk.shopamos.rest.model.entity.OrderProduct;
import drk.shopamos.rest.model.entity.Product;
import drk.shopamos.rest.model.enumerable.OrderStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnitUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
class OrderRepositoryTest {
    @Autowired private OrderRepository testee;

    @PersistenceContext private EntityManager entityManager;

    private PersistenceUnitUtil persistenceUnitUtil;

    @Test
    @DisplayName("save - when valid data then save to db")
    void save_whenValidData_saveToDb() {
        Order order = buildNewOrderWithTwoItems();
        testee.saveAndFlush(order);
        assertOrder(order);
    }

    @Test
    @DisplayName("save - when product ID is repeated on same order then throw exception")
    void save_whenRepeatedProductId_thenThrowException() {
        Order order = buildNewOrderWithTwoItems();
        order.getOrderProducts().get(0).getProduct().setId(1);
        order.getOrderProducts().get(1).getProduct().setId(1);
        assertThrows(DataIntegrityViolationException.class, () -> testee.saveAndFlush(order));
    }

    @Test
    @DisplayName("save - when status is null then throw exception")
    void save_whenNullStatus_thenThrowException() {
        Order order = buildNewOrderWithTwoItems();
        order.setStatus(null);
        assertThrows(DataIntegrityViolationException.class, () -> testee.saveAndFlush(order));
    }

    @Test
    @DisplayName("save - when createdDate is null then throw exception")
    void save_whenCreatedDateNull_thenThrowException() {
        Order order = buildNewOrderWithTwoItems();
        order.setCreatedDate(null);
        assertThrows(DataIntegrityViolationException.class, () -> testee.saveAndFlush(order));
    }

    @Test
    @DisplayName("save - when updatedDate is null then throw exception")
    void save_whenUpdatedDateNull_thenThrowException() {
        Order order = buildNewOrderWithTwoItems();
        order.setUpdatedDate(null);
        assertThrows(DataIntegrityViolationException.class, () -> testee.saveAndFlush(order));
    }

    @Test
    @DisplayName("save - when user is null then throw exception")
    void save_whenUserNull_thenThrowException() {
        Order order = buildNewOrderWithTwoItems();
        order.setUser(null);
        assertThrows(DataIntegrityViolationException.class, () -> testee.saveAndFlush(order));
    }

    @Test
    @DisplayName("save - when price has over 9 precision then throw error")
    void save_whenPriceWrongPrecisionAndScale_throwError() {
        Order order = testee.getReferenceById(1);
        order.getOrderProducts().get(0).setUnitPrice(BigDecimal.valueOf(1234567890));
        assertThrows(DataIntegrityViolationException.class, () -> testee.saveAndFlush(order));
    }

    @Test
    @DisplayName("save - price scale is rounded up to 2 decimal points")
    void save_priceScaleIsRoundedUpTo_2DecimalPoints_throwError() {
        Order order = testee.getReferenceById(1);
        order.getOrderProducts().get(0).setUnitPrice(BigDecimal.valueOf(12.1267));
        testee.saveAndFlush(order);
        entityManager.clear();
        assertThat(
                testee.getReferenceById(1).getOrderProducts().get(0).getUnitPrice().scale(), is(2));
    }

    @Test
    @DisplayName("findById - user and orderProduct are associated and lazy loaded")
    void findById_user_and_orderProduct_lazyLoaded() {
        persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();

        Order order = testee.findById(1).orElseThrow();
        assertUserLazyLoaded(order.getUser());
        assertOrderProductLazyLoaded(order.getOrderProducts());
        assertProductLazyLoaded(order.getOrderProducts().get(0).getProduct(), SHUSUI_PROD_NAME);
        assertProductLazyLoaded(order.getOrderProducts().get(1).getProduct(), GMERRY_PROD_NAME);
    }

    @Test
    @DisplayName("findAllByAttributes - when all params are null, return all orders")
    void findAllByAttributes_whenParamsAreNull_returnAllOrders() {
        List<Order> orders = testee.findAllByAttributes(null, null, null, null, null, null);
        assertThat(orders.size(), is(2));
    }

    @Test
    @DisplayName("findAllByAttributes - searches by username contains and case insensitive")
    void findAllByAttributes_searchesByUsername() {
        List<Order> orders = testee.findAllByAttributes("uFfY", null, null, null, null, null);
        assertThat(orders.size(), is(1));
        assertThat(orders.get(0).getUser().getEmail(), is(LUFFY_EMAIL));
    }

    @Test
    @DisplayName("findAllByAttributes - searches by orderStatus")
    void findAllByAttributes_searchesByOrderStatus() {
        List<Order> orders =
                testee.findAllByAttributes(null, OrderStatus.SUBMITTED, null, null, null, null);
        assertThat(orders.size(), is(1));
        assertThat(orders.get(0).getStatus(), is(OrderStatus.SUBMITTED));
    }

    @Test
    @DisplayName("findAllByAttributes - searches updatedDate greater than")
    void findAllByAttributes_searchesFromUpdatedDate() {
        List<Order> orders =
                testee.findAllByAttributes(
                        null, null, DECEMBER_2ND_1PM.minusSeconds(1), null, null, null);
        assertThat(orders.size(), is(1));
        assertThat(orders.get(0).getUpdatedDate(), is(DECEMBER_2ND_1PM));
    }

    @Test
    @DisplayName("findAllByAttributes - searches updated date less than")
    void findAllByAttributes_searchesToUpdatedDate() {
        List<Order> orders =
                testee.findAllByAttributes(
                        null, null, null, NOVEMBER_4TH_3PM.plusSeconds(1), null, null);
        assertThat(orders.size(), is(1));
        assertThat(orders.get(0).getUpdatedDate(), is(NOVEMBER_4TH_3PM));
    }

    @Test
    @DisplayName("findAllByAttributes - ignores productIds when array is empty")
    void findAllByAttributes_ignoresProductIdsWhenArrayIsEmpty() {
        List<Order> orders =
                testee.findAllByAttributes(null, null, null, null, new ArrayList<>(), 0);
        assertThat(orders.size(), is(2));
    }

    @Test
    @DisplayName("findAllByAttributes - searches by orders containing all productIds in list")
    void findAllByAttributes_searchesByProductIds() {
        List<Order> orders = testee.findAllByAttributes(null, null, null, null, List.of(1, 2), 2);
        assertThat(orders.size(), is(1));
        assertThat(orders.get(0).getUser().getEmail(), is(LUFFY_EMAIL));
    }

    @Test
    @DisplayName("findAllByAttributes - searches equals on all attributes")
    void findAllByAttributes_searchesAllAttributes() {
        List<Order> orders =
                testee.findAllByAttributes(
                        LUFFY_EMAIL,
                        OrderStatus.SUBMITTED,
                        NOVEMBER_4TH_3PM,
                        NOVEMBER_4TH_3PM,
                        List.of(1, 2),
                        2);
        assertThat(orders.size(), is(1));
        assertThat(orders.get(0).getUser().getEmail(), is(LUFFY_EMAIL));
        assertThat(orders.get(0).getStatus(), is(OrderStatus.SUBMITTED));
        assertThat(orders.get(0).getUpdatedDate(), is(NOVEMBER_4TH_3PM));
    }

    @Test
    @DisplayName("delete - deletes order cascading orderProducts")
    void delete_alsoDeletesOrderProducts() {
        Order order = testee.findById(1).orElseThrow();
        testee.delete(order);
        entityManager.flush();
    }

    private void assertOrder(Order order) {
        assertThat(order.getId(), is(notNullValue()));
        assertThat(order.getCreatedDate(), is(ORD_CREATED_DATE));
        assertThat(order.getUpdatedDate(), is(ORD_UPDATED_DATE));
        assertThat(order.getUser().getId(), is(LUFFY_ID));
        assertThat(order.getStatus(), is(ORD_STATUS));
        assertThat(order.getOrderProducts().size(), is(2));
        assertThat(order.getOrderProducts().get(0).getId(), is(notNullValue()));
        assertThat(order.getOrderProducts().get(1).getId(), is(notNullValue()));
    }

    private void assertUserLazyLoaded(Account user) {
        assertThat(persistenceUnitUtil.isLoaded(user), is(false));
        assertThat(user.getName(), is(LUFFY_NAME));
        assertThat(persistenceUnitUtil.isLoaded(user), is(true));
    }

    private void assertOrderProductLazyLoaded(List<OrderProduct> orderProducts) {
        assertThat(persistenceUnitUtil.isLoaded(orderProducts), is(false));
        assertThat(orderProducts.size(), is(2));
        assertThat(persistenceUnitUtil.isLoaded(orderProducts), is(true));
    }

    private void assertProductLazyLoaded(Product product, String productName) {
        assertThat(persistenceUnitUtil.isLoaded(product), is(false));
        assertThat(product.getName(), is(productName));
        assertThat(persistenceUnitUtil.isLoaded(product), is(true));
    }
}
