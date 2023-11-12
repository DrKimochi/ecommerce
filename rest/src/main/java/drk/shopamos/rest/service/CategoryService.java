package drk.shopamos.rest.service;

import static drk.shopamos.rest.config.MessageProvider.MSG_EXISTS_CATEGORY;

import drk.shopamos.rest.config.MessageProvider;
import drk.shopamos.rest.model.entity.Category;
import drk.shopamos.rest.repository.CategoryRepository;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;

@Service
public class CategoryService extends BaseService {
    private final CategoryRepository repository;

    public CategoryService(
            CategoryRepository categoryRepository, MessageProvider messageProvider, Clock clock) {
        super(messageProvider, clock);
        this.repository = categoryRepository;
    }

    public Category createCategory(Category category) {
        String id = category.getId();
        if (repository.findById(id).isPresent()) {
            throw anEntityExistsException(MSG_EXISTS_CATEGORY, id).get();
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
        Category category = getCategory(id);
        repository.delete(category);
    }

    public Category getCategory(String id) {
        return repository.findById(id).orElseThrow(anEntityNotFoundException(id));
    }

    public List<Category> getCategories(String name, String description) {
        return repository.findAllByAttributes(name, description);
    }
}
