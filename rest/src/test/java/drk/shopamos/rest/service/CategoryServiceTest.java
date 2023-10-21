package drk.shopamos.rest.service;

import static drk.shopamos.rest.mother.CategoryMother.MISC_CAT_ID;
import static drk.shopamos.rest.mother.CategoryMother.buildMiscCategory;
import static drk.shopamos.rest.mother.MessageMother.MSG_EXISTS_CATEGORY;
import static drk.shopamos.rest.mother.MessageMother.MSG_NOT_FOUND_ID;

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
        when(repository.findById(MISC_CAT_ID)).thenReturn(Optional.of(new Category()));
        assertThrows(EntityExistsException.class, () -> testee.createCategory(misc));
        verify(messageProvider).getMessage(MSG_EXISTS_CATEGORY, MISC_CAT_ID);
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
        assertThrows(EntityNotFoundException.class, () -> testee.updateCategory(misc));
        verify(messageProvider).getMessage(MSG_NOT_FOUND_ID, MISC_CAT_ID);
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
        assertThrows(EntityNotFoundException.class, () -> testee.deleteCategory(MISC_CAT_ID));
        verify(messageProvider).getMessage(MSG_NOT_FOUND_ID, MISC_CAT_ID);
        verify(repository, times(0)).save(any());
    }

    @Test
    @DisplayName("deleteCategory - deletes by ID when the ID exists")
    void deleteCategory_deletesById_whenIdExists() {
        when(repository.findById(MISC_CAT_ID)).thenReturn(Optional.of(new Category()));
        testee.deleteCategory(MISC_CAT_ID);
        verify(repository).deleteById(MISC_CAT_ID);
    }
}
