package drk.shopamos.rest.service;

import static drk.shopamos.rest.config.MessageProvider.MSG_EXISTS_CATEGORY;
import static drk.shopamos.rest.config.MessageProvider.MSG_NOT_FOUND_ID;

import drk.shopamos.rest.config.MessageProvider;
import drk.shopamos.rest.model.entity.Category;
import drk.shopamos.rest.repository.CategoryRepository;
import drk.shopamos.rest.service.exception.EntityExistsException;
import drk.shopamos.rest.service.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repository;
    private final MessageProvider msgProvider;

    public Category createCategory(Category category) {
        String id = category.getId();
        if (repository.findById(id).isPresent()) {
            throw anEntityExistsException(id).get();
        }
        return repository.save(category);
    }

    public Category updateCategory(Category category) {
        String id = category.getId();
        if (repository.findById(id).isEmpty()) {
            throw anEntityNotFoundException(id).get();
        }
        return repository.save(category);
    }

    public void deleteCategory(String id) {
        if (repository.findById(id).isEmpty()) {
            throw anEntityNotFoundException(id).get();
        }
        repository.deleteById(id);
    }

    private Supplier<EntityExistsException> anEntityExistsException(String categoryId) {
        return () ->
                new EntityExistsException(msgProvider.getMessage(MSG_EXISTS_CATEGORY, categoryId));
    }

    private Supplier<EntityNotFoundException> anEntityNotFoundException(String id) {
        return () -> new EntityNotFoundException(msgProvider.getMessage(MSG_NOT_FOUND_ID, id));
    }
}
