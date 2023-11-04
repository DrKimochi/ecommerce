package drk.shopamos.rest.repository;

import static drk.shopamos.rest.mother.AccountMother.LUFFY_ID;
import static drk.shopamos.rest.mother.AccountMother.LUFFY_NAME;
import static drk.shopamos.rest.mother.OrderMother.ORD_CREATED_DATE;
import static drk.shopamos.rest.mother.OrderMother.ORD_STATUS;
import static drk.shopamos.rest.mother.OrderMother.ORD_UPDATED_DATE;
import static drk.shopamos.rest.mother.OrderMother.buildOrderWithTwoItems;
import static drk.shopamos.rest.mother.ProductMother.GMERRY_PROD_NAME;
import static drk.shopamos.rest.mother.ProductMother.SHUSUI_PROD_NAME;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.model.entity.Order;
import drk.shopamos.rest.model.entity.OrderProduct;
import drk.shopamos.rest.model.entity.Product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnitUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@DataJpaTest
class OrderRepositoryTest {
    @Autowired private OrderRepository testee;

    @PersistenceContext private EntityManager entityManager;

    private PersistenceUnitUtil persistenceUnitUtil;

    @Test
    @DisplayName("save - when valid data then save to db")
    void save_whenValidData_saveToDb() {
        Order order = buildOrderWithTwoItems();
        order.setId(null);
        testee.saveAndFlush(order);
        assertOrder(order);
    }

    @Test
    @DisplayName("save - when status is null then throw exception")
    void save_whenNullStatus_thenThrowException() {
        Order order = buildOrderWithTwoItems();
        order.setStatus(null);
        assertThrows(DataIntegrityViolationException.class, () -> testee.saveAndFlush(order));
    }

    @Test
    @DisplayName("save - when createdDate is null then throw exception")
    void save_whenCreatedDateNull_thenThrowException() {
        Order order = buildOrderWithTwoItems();
        order.setCreatedDate(null);
        assertThrows(DataIntegrityViolationException.class, () -> testee.saveAndFlush(order));
    }

    @Test
    @DisplayName("save - when updatedDate is null then throw exception")
    void save_whenUpdatedDateNull_thenThrowException() {
        Order order = buildOrderWithTwoItems();
        order.setUpdatedDate(null);
        assertThrows(DataIntegrityViolationException.class, () -> testee.saveAndFlush(order));
    }

    @Test
    @DisplayName("save - when user is null then throw exception")
    void save_whenUserNull_thenThrowException() {
        Order order = buildOrderWithTwoItems();
        order.setUser(null);
        assertThrows(DataIntegrityViolationException.class, () -> testee.saveAndFlush(order));
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

    private void assertOrder(Order order) {
        assertThat(order.getId(), is(notNullValue()));
        assertThat(order.getCreatedDate(), is(ORD_CREATED_DATE));
        assertThat(order.getUpdatedDate(), is(ORD_UPDATED_DATE));
        assertThat(order.getUser().getId(), is(LUFFY_ID));
        assertThat(order.getStatus(), is(ORD_STATUS));
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
