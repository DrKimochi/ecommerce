package drk.shopamos.rest.repository;

import static drk.shopamos.rest.mother.CategoryMother.FRUIT_CAT_ID;
import static drk.shopamos.rest.mother.CategoryMother.HATS_CAT_ID;
import static drk.shopamos.rest.mother.CategoryMother.MISC_CAT_ID;
import static drk.shopamos.rest.mother.CategoryMother.SWORD_CAT_ID;
import static drk.shopamos.rest.mother.CategoryMother.buildFruitCategory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import drk.shopamos.rest.argument.CategoryFindAllByAttributesArguments;
import drk.shopamos.rest.model.entity.Category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;

import java.util.List;
import java.util.Optional;

@DataJpaTest
class CategoryRepositoryTest {
    @Autowired private CategoryRepository testee;

    @Test
    @DisplayName("save - when category is valid, then it is saved to database ")
    void save_whenValidCategory_thenSavesToDb() {
        Category fruitCategory = buildFruitCategory();
        testee.saveAndFlush(fruitCategory);
        Optional<Category> expectedSavedCategoryOpt = testee.findById(FRUIT_CAT_ID);
        assertThat(expectedSavedCategoryOpt.isPresent(), is(true));
        assertThat(expectedSavedCategoryOpt.get(), is(fruitCategory));
    }

    @Test
    @DisplayName("save - when id greater than 4 characters then throw error ")
    void save_wheIdTooLong_thenThrowError() {
        Category fruitCategory = buildFruitCategory();
        fruitCategory.setId("FRUIT");
        assertThrows(
                DataIntegrityViolationException.class, () -> testee.saveAndFlush(fruitCategory));
    }

    @Test
    @DisplayName("save - when id is null then throw error ")
    void save_whenIdNull_thenThrowError() {
        Category fruitCategory = buildFruitCategory();
        fruitCategory.setId(null);
        assertThrows(JpaSystemException.class, () -> testee.saveAndFlush(fruitCategory));
    }

    @Test
    @DisplayName("save - when name is missing then throw error ")
    void save_whenNameMissing_thenThrowError() {
        Category fruitCategory = buildFruitCategory();
        fruitCategory.setName(null);
        assertThrows(
                DataIntegrityViolationException.class, () -> testee.saveAndFlush(fruitCategory));
    }

    @Test
    @DisplayName("deleteById - when ID exists then row is deleted")
    void deleteById_whenIdExists_thenDeleteRow() {
        assertThat(testee.existsById(HATS_CAT_ID), is(true));
        testee.deleteById(HATS_CAT_ID);
        assertThat(testee.existsById(HATS_CAT_ID), is(false));
    }

    @Test
    @DisplayName("deleteById - when ID exists but there are products linked then throw exception")
    void deleteById_whenIdExistsWithProduct_thenThrowException() {
        assertThat(testee.existsById(SWORD_CAT_ID), is(true));
        testee.deleteById(SWORD_CAT_ID);
        assertThrows(DataIntegrityViolationException.class, () -> testee.existsById(SWORD_CAT_ID));
    }

    @ParameterizedTest
    @ArgumentsSource(CategoryFindAllByAttributesArguments.class)
    @DisplayName("findAllByAttributes - finds by name/description contains and case insensitive")
    void findAllByAttributes_findsByAttributes(String name, String description) {
        List<Category> foundCategories = testee.findAllByAttributes(name, description);
        assertThat(foundCategories.size(), is(1));
        assertThat(foundCategories.get(0).getId(), is(MISC_CAT_ID));
    }

    @Test
    @DisplayName("findAllByAttributes - returns all categories when attributes all null")
    void findAllByAttributes_returnsAllCategories() {
        List<Category> foundCategories = testee.findAllByAttributes(null, null);
        assertThat(foundCategories.size(), is(4));
    }
}
