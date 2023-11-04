package drk.shopamos.rest.service;

import static drk.shopamos.rest.mother.CategoryMother.FRUIT_CAT_DESC;
import static drk.shopamos.rest.mother.CategoryMother.FRUIT_CAT_ID;
import static drk.shopamos.rest.mother.CategoryMother.FRUIT_CAT_NAME;
import static drk.shopamos.rest.mother.CategoryMother.MISC_CAT_ID;
import static drk.shopamos.rest.mother.CategoryMother.buildFruitCategory;
import static drk.shopamos.rest.mother.CategoryMother.buildMiscCategory;
import static drk.shopamos.rest.mother.MessageMother.MSG_EXISTS_CATEGORY;
import static drk.shopamos.rest.mother.MessageMother.MSG_NOT_FOUND_ID;
import static drk.shopamos.rest.mother.MessageMother.RETURNED_MSG;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import drk.shopamos.rest.config.MessageProvider;
import drk.shopamos.rest.model.entity.Category;
import drk.shopamos.rest.repository.CategoryRepository;
import drk.shopamos.rest.service.exception.EntityExistsException;
import drk.shopamos.rest.service.exception.EntityNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock CategoryRepository repository;
    @Mock MessageProvider messageProvider;
    @InjectMocks CategoryService testee;

    @Test
    @DisplayName("createCategory - throws exception when category already exists")
    void createCategory_throwsExceptionWhenCategoryAlreadyExists() {
        Category misc = buildMiscCategory();
        when(messageProvider.getMessage(MSG_EXISTS_CATEGORY, MISC_CAT_ID)).thenReturn(RETURNED_MSG);
        when(repository.findById(MISC_CAT_ID)).thenReturn(Optional.of(new Category()));
        String exceptionMsg =
                assertThrows(EntityExistsException.class, () -> testee.createCategory(misc))
                        .getMessage();
        assertThat(RETURNED_MSG, is(exceptionMsg));
        verify(repository, times(0)).save(any());
    }

    @Test
    @DisplayName("createCategory - saves category when it doesnt exist yet")
    void createCategory_savesWhenCategoryDoesntExistYet() {
        Category misc = buildMiscCategory();
        when(repository.findById(MISC_CAT_ID)).thenReturn(Optional.empty());
        testee.createCategory(misc);
        verify(repository).save(misc);
    }

    @Test
    @DisplayName("updateCategory -throws exception when ID does not exist")
    void updateCategory_throwsExceptionWhenIdDoesNotExist() {
        Category misc = buildMiscCategory();
        when(repository.findById(MISC_CAT_ID)).thenReturn(Optional.empty());
        when(messageProvider.getMessage(MSG_NOT_FOUND_ID, MISC_CAT_ID)).thenReturn(RETURNED_MSG);
        String exceptionMsg =
                assertThrows(EntityNotFoundException.class, () -> testee.updateCategory(misc))
                        .getMessage();
        assertThat(RETURNED_MSG, is(exceptionMsg));
        verify(repository, times(0)).save(any());
    }

    @Test
    @DisplayName("updateCategory - save Category with encoded password when validations pass")
    void updateCategory_savesWhenValidationsPass() {
        Category misc = buildMiscCategory();
        when(repository.findById(MISC_CAT_ID)).thenReturn(Optional.of(new Category()));
        testee.updateCategory(misc);
        verify(repository).save(misc);
    }

    @Test
    @DisplayName("deleteCategory - throws exception when ID does not exist")
    void deleteCategory_throwsExceptionWhenIdDoesNotExist() {
        when(repository.findById(MISC_CAT_ID)).thenReturn(Optional.empty());
        when(messageProvider.getMessage(MSG_NOT_FOUND_ID, MISC_CAT_ID)).thenReturn(RETURNED_MSG);
        String exceptionMsg =
                assertThrows(
                                EntityNotFoundException.class,
                                () -> testee.deleteCategory(MISC_CAT_ID))
                        .getMessage();
        assertThat(RETURNED_MSG, is(exceptionMsg));
        verify(repository, times(0)).save(any());
    }

    @Test
    @DisplayName("deleteCategory - deletes by ID when the ID exists")
    void deleteCategory_deletesById_whenIdExists() {
        when(repository.findById(MISC_CAT_ID)).thenReturn(Optional.of(new Category()));
        testee.deleteCategory(MISC_CAT_ID);
        verify(repository).deleteById(MISC_CAT_ID);
    }

    @Test
    @DisplayName("getCategory - throws exception when category not found")
    void getCategory_throwsExceptionWhenNotFound() {
        when(repository.findById(FRUIT_CAT_ID)).thenReturn(Optional.empty());
        when(messageProvider.getMessage(MSG_NOT_FOUND_ID, FRUIT_CAT_ID)).thenReturn(RETURNED_MSG);
        String exceptionMsg =
                assertThrows(EntityNotFoundException.class, () -> testee.getCategory(FRUIT_CAT_ID))
                        .getMessage();
        assertThat(RETURNED_MSG, is(exceptionMsg));
    }

    @Test
    @DisplayName("getCategory - returns category when it is found")
    void getCategory_returnsCategory() {
        Category fruitCategory = buildFruitCategory();
        when(repository.findById(FRUIT_CAT_ID)).thenReturn(Optional.of(fruitCategory));
        Category foundCategory = testee.getCategory(FRUIT_CAT_ID);
        assertThat(foundCategory, is(fruitCategory));
    }

    @Test
    @DisplayName(
            ("getCategories - invokes repository findAllByAttributes passing down the parameters"))
    void getCategories_invokes_findAllByAttributes() {
        testee.getCategories(FRUIT_CAT_NAME, FRUIT_CAT_DESC);
        verify(repository).findAllByAttributes(FRUIT_CAT_NAME, FRUIT_CAT_DESC);
    }
}
