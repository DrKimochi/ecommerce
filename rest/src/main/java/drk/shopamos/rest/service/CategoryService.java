package drk.shopamos.rest.service;

import drk.shopamos.rest.model.entity.Category;
import drk.shopamos.rest.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category createCategory(Category category) {

        return null;
    }
}
