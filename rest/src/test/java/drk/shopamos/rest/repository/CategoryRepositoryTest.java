package drk.shopamos.rest.repository;

import static drk.shopamos.rest.mother.CategoryMother.FRUIT_CAT_ID;
import static drk.shopamos.rest.mother.CategoryMother.buildFruitCategory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import drk.shopamos.rest.model.entity.Category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;

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
}
