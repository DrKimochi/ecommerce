package drk.shopamos.rest.repository;

import static drk.shopamos.rest.mother.CategoryMother.SHIP_CAT_NAME;
import static drk.shopamos.rest.mother.ProductMother.buildThousandSunny;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

import drk.shopamos.rest.model.entity.Product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnitUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired private ProductRepository testee;

    @PersistenceContext private EntityManager entityManager;

    @Test
    @DisplayName("save - when valid data then save to db")
    void save_whenValidData_saveToDb() {
        Product product = buildThousandSunny();
        product.setId(null);
        testee.saveAndFlush(product);
        assertThat(product.getId(), is(notNullValue()));
    }

    @Test
    @DisplayName("save - when name missing then throw error")
    void save_whenNameMissing_throwError() {
        Product product = buildThousandSunny();
        product.setName(null);
        assertThrows(DataIntegrityViolationException.class, () -> testee.saveAndFlush(product));
    }

    @Test
    @DisplayName("save - when price missing then throw error")
    void save_whenPriceMissing_throwError() {
        Product product = buildThousandSunny();
        product.setPrice(null);
        assertThrows(DataIntegrityViolationException.class, () -> testee.saveAndFlush(product));
    }

    @Test
    @DisplayName("save - when price has over 9 precision then throw error")
    void save_whenPriceWrongPrecisionAndScale_throwError() {
        Product product = buildThousandSunny();
        product.setPrice(BigDecimal.valueOf(1234567890));
        assertThrows(DataIntegrityViolationException.class, () -> testee.saveAndFlush(product));
    }

    @Test
    @DisplayName("save - price scale is rounded up to 2 decimal points")
    void save_priceScaleIsRoundedUpTo_2DecimalPoints_throwError() {
        Product product = testee.getReferenceById(1);
        product.setPrice(BigDecimal.valueOf(12.1267));
        testee.saveAndFlush(product);
        entityManager.clear();
        assertThat(testee.getReferenceById(1).getPrice().scale(), is(2));
    }

    @Test
    @DisplayName("save - when category missing then throw error")
    void save_whenCategoryMissing_throwError() {
        Product product = buildThousandSunny();
        product.setCategory(null);
        assertThrows(DataIntegrityViolationException.class, () -> testee.saveAndFlush(product));
    }

    @Test
    @DisplayName("save - when category doesnt exist then throw error")
    void save_whenCategoryDoesntExist_throwError() {
        Product product = buildThousandSunny();
        product.getCategory().setId("CATS");
        assertThrows(DataIntegrityViolationException.class, () -> testee.saveAndFlush(product));
    }

    @Test
    @DisplayName("findById - categories are associated and are lazy loaded")
    void findById_categoriesAreLazyLoaded() {
        PersistenceUnitUtil persistenceUnitUtil =
                entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        Product product = testee.findById(1).orElseThrow();
        assertThat(persistenceUnitUtil.isLoaded(product.getCategory()), is(false));
        assertThat(product.getCategory().getName(), is(SHIP_CAT_NAME));
        assertThat(persistenceUnitUtil.isLoaded(product.getCategory()), is(true));
    }
}
