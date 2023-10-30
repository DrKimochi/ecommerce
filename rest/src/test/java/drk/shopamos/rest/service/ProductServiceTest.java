package drk.shopamos.rest.service;

import static drk.shopamos.rest.config.MessageProvider.MSG_NOT_FOUND_ID;
import static drk.shopamos.rest.mother.CategoryMother.SHIP_CAT_ID;
import static drk.shopamos.rest.mother.CategoryMother.buildShipCategory;
import static drk.shopamos.rest.mother.MessageMother.MSG_NOT_FOUND_CATEGORY;
import static drk.shopamos.rest.mother.MessageMother.RETURNED_MSG;
import static drk.shopamos.rest.mother.ProductMother.TSUNNY_PROD_DESC;
import static drk.shopamos.rest.mother.ProductMother.TSUNNY_PROD_ID;
import static drk.shopamos.rest.mother.ProductMother.TSUNNY_PROD_NAME;
import static drk.shopamos.rest.mother.ProductMother.buildThousandSunny;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import drk.shopamos.rest.config.MessageProvider;
import drk.shopamos.rest.model.entity.Category;
import drk.shopamos.rest.model.entity.Product;
import drk.shopamos.rest.repository.CategoryRepository;
import drk.shopamos.rest.repository.ProductRepository;
import drk.shopamos.rest.service.exception.EntityNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock CategoryRepository categoryRepository;
    @Mock ProductRepository productRepository;
    @Mock MessageProvider messageProvider;
    @InjectMocks ProductService testee;

    @Test
    @DisplayName("createProduct - throws entity not found when category does not exist")
    void createProduct_whenCategoryNotFound_throwsException() {
        Product product = buildThousandSunny();
        when(messageProvider.getMessage(MSG_NOT_FOUND_CATEGORY, SHIP_CAT_ID))
                .thenReturn(RETURNED_MSG);
        assertThrows(
                EntityNotFoundException.class, () -> testee.createProduct(SHIP_CAT_ID, product));
    }

    @Test
    @DisplayName("createProduct - saves product when category exists")
    void createProduct_saves() {
        Category category = buildShipCategory();
        Product product = buildThousandSunny();
        product.setCategory(null);

        when(categoryRepository.existsById(SHIP_CAT_ID)).thenReturn(true);
        when(categoryRepository.getReferenceById(SHIP_CAT_ID)).thenReturn(category);
        testee.createProduct(SHIP_CAT_ID, product);
        assertThat(product.getCategory(), is(category));
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("editProduct - throws entity not found when product ID does not exist")
    void editProduct_whenProductIdNotFound_throwsException() {
        Product product = buildThousandSunny();

        when(productRepository.findById(TSUNNY_PROD_ID)).thenReturn(Optional.empty());
        when(messageProvider.getMessage(MSG_NOT_FOUND_ID, TSUNNY_PROD_ID)).thenReturn(RETURNED_MSG);
        String exceptionMsg =
                assertThrows(
                                EntityNotFoundException.class,
                                () -> testee.editProduct(SHIP_CAT_ID, product))
                        .getMessage();
        assertThat(RETURNED_MSG, is(exceptionMsg));
    }

    @Test
    @DisplayName("editProduct - throws entity not found when category does not exist")
    void editProduct_whenCategoryNotFound_throwsException() {
        Product product = buildThousandSunny();
        when(productRepository.findById(TSUNNY_PROD_ID)).thenReturn(Optional.of(product));
        when(messageProvider.getMessage(MSG_NOT_FOUND_CATEGORY, SHIP_CAT_ID))
                .thenReturn(RETURNED_MSG);
        String exceptionMsg =
                assertThrows(
                                EntityNotFoundException.class,
                                () -> testee.editProduct(SHIP_CAT_ID, product))
                        .getMessage();
        assertThat(RETURNED_MSG, is(exceptionMsg));
    }

    @Test
    @DisplayName("editProduct - saves when product ID and category ID exists")
    void editProduct_saves() {
        Category category = buildShipCategory();
        Product product = buildThousandSunny();
        product.setCategory(null);

        when(productRepository.findById(TSUNNY_PROD_ID)).thenReturn(Optional.of(product));
        when(categoryRepository.getReferenceById(SHIP_CAT_ID)).thenReturn(category);
        when(categoryRepository.existsById(SHIP_CAT_ID)).thenReturn(true);
        testee.editProduct(SHIP_CAT_ID, product);
        assertThat(product.getCategory(), is(category));
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("deleteProduct - throws entity not found when productID does not exist")
    void deleteProduct_whenProductIdNotFound_throwsException() {

        when(productRepository.findById(TSUNNY_PROD_ID)).thenReturn(Optional.empty());
        when(messageProvider.getMessage(MSG_NOT_FOUND_ID, TSUNNY_PROD_ID)).thenReturn(RETURNED_MSG);
        String exceptionMsg =
                assertThrows(
                                EntityNotFoundException.class,
                                () -> testee.deleteProduct(TSUNNY_PROD_ID))
                        .getMessage();
        assertThat(RETURNED_MSG, is(exceptionMsg));
    }

    @Test
    @DisplayName("deleteProduct - deletes when productID is found")
    void deleteProduct_deletesProduct() {
        Product product = buildThousandSunny();
        when(productRepository.findById(TSUNNY_PROD_ID)).thenReturn(Optional.of(product));
        testee.deleteProduct(TSUNNY_PROD_ID);
        verify(productRepository).deleteById(TSUNNY_PROD_ID);
    }

    @Test
    @DisplayName("getProduct - gets Product when ID exists")
    void getProduct_returnsProductWhenIdExists() {
        Product product = buildThousandSunny();
        when(productRepository.findById(TSUNNY_PROD_ID)).thenReturn(Optional.of(product));
        Product foundProduct = testee.getProduct(TSUNNY_PROD_ID);
        assertThat(foundProduct, is(product));
    }

    @Test
    @DisplayName("getProduct - throws exception when Product Id does not exist")
    void getProduct_throwsExceptionWhenProductIdDoesNotExist() {
        when(productRepository.findById(TSUNNY_PROD_ID)).thenReturn(Optional.empty());
        when(messageProvider.getMessage(MSG_NOT_FOUND_ID, TSUNNY_PROD_ID)).thenReturn(RETURNED_MSG);
        String exceptionMsg =
                assertThrows(EntityNotFoundException.class, () -> testee.getProduct(TSUNNY_PROD_ID))
                        .getMessage();
        assertThat(RETURNED_MSG, is(exceptionMsg));
    }

    @Test
    @DisplayName("getActiveProduct - gets Product when ID exists")
    void getActiveProduct_returnsProductWhenIdExists() {
        Product product = buildThousandSunny();
        when(productRepository.findById(TSUNNY_PROD_ID)).thenReturn(Optional.of(product));
        Product foundProduct = testee.getActiveProduct(TSUNNY_PROD_ID);
        assertThat(foundProduct, is(product));
    }

    @Test
    @DisplayName("getActiveProduct - throws exception when Product Id does not exist")
    void getActiveProduct_throwsExceptionWhenProductIdDoesNotExist() {
        when(productRepository.findById(TSUNNY_PROD_ID)).thenReturn(Optional.empty());
        when(messageProvider.getMessage(MSG_NOT_FOUND_ID, TSUNNY_PROD_ID)).thenReturn(RETURNED_MSG);
        String exceptionMsg =
                assertThrows(
                                EntityNotFoundException.class,
                                () -> testee.getActiveProduct(TSUNNY_PROD_ID))
                        .getMessage();
        assertThat(RETURNED_MSG, is(exceptionMsg));
    }

    @Test
    @DisplayName("getActiveProduct - throws exception when Product Id does not exist")
    void getActiveProduct_throwsExceptionWhenProductIsNotActive() {
        Product product = buildThousandSunny();
        product.setActive(false);
        when(productRepository.findById(TSUNNY_PROD_ID)).thenReturn(Optional.of(product));
        when(messageProvider.getMessage(MSG_NOT_FOUND_ID, TSUNNY_PROD_ID)).thenReturn(RETURNED_MSG);
        String exceptionMsg =
                assertThrows(
                                EntityNotFoundException.class,
                                () -> testee.getActiveProduct(TSUNNY_PROD_ID))
                        .getMessage();
        assertThat(RETURNED_MSG, is(exceptionMsg));
    }

    @Test
    @DisplayName("getProducts - invokes findAllByAttributes passing down the parameters")
    void getProducts_invokes_findAllByAttributes() {
        Product product = buildThousandSunny();
        List<Product> products = List.of(product);
        when(productRepository.findAllByAttributes(
                        SHIP_CAT_ID,
                        TSUNNY_PROD_NAME,
                        TSUNNY_PROD_DESC,
                        BigDecimal.ONE,
                        BigDecimal.TEN,
                        true))
                .thenReturn(products);
        List<Product> foundProducts =
                testee.getProducts(
                        SHIP_CAT_ID,
                        TSUNNY_PROD_NAME,
                        TSUNNY_PROD_DESC,
                        BigDecimal.ONE,
                        BigDecimal.TEN,
                        true);
        assertThat(foundProducts.size(), is(1));
        assertThat(product, is(foundProducts.get(0)));
    }
}
