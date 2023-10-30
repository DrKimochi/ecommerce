package drk.shopamos.rest.service;

import static drk.shopamos.rest.config.MessageProvider.MSG_NOT_FOUND_CATEGORY;
import static drk.shopamos.rest.config.MessageProvider.MSG_NOT_FOUND_ID;

import drk.shopamos.rest.config.MessageProvider;
import drk.shopamos.rest.model.entity.Product;
import drk.shopamos.rest.repository.CategoryRepository;
import drk.shopamos.rest.repository.ProductRepository;
import drk.shopamos.rest.service.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final MessageProvider msgProvider;

    public Product createProduct(String categoryId, Product product) {
        return saveProduct(categoryId, product);
    }

    public Product editProduct(String newCategoryId, Product product) {
        Integer id = product.getId();
        if (productRepository.findById(id).isEmpty()) {
            throw anEntityNotFoundException(id).get();
        }
        return saveProduct(newCategoryId, product);
    }

    public void deleteProduct(Integer id) {
        if (productRepository.findById(id).isEmpty()) {
            throw anEntityNotFoundException(id).get();
        }
        productRepository.deleteById(id);
    }

    public Product getProduct(Integer id) {
        return productRepository.findById(id).orElseThrow(anEntityNotFoundException(id));
    }

    public Product getActiveProduct(Integer id) {
        Product product = getProduct(id);
        if (!product.isActive()) {
            throw anEntityNotFoundException(id).get();
        }
        return product;
    }

    public List<Product> getProducts(
            String categoryId,
            String name,
            String description,
            BigDecimal priceFrom,
            BigDecimal priceTo,
            Boolean isActive) {
        return productRepository.findAllByAttributes(
                categoryId, name, description, priceFrom, priceTo, isActive);
    }

    private Product saveProduct(String categoryId, Product product) {
        if (!categoryRepository.existsById(categoryId)) {
            throw anEntityNotFoundException(categoryId).get();
        }

        product.setCategory(categoryRepository.getReferenceById(categoryId));
        return productRepository.save(product);
    }

    private Supplier<EntityNotFoundException> anEntityNotFoundException(String id) {
        return () ->
                new EntityNotFoundException(msgProvider.getMessage(MSG_NOT_FOUND_CATEGORY, id));
    }

    private Supplier<EntityNotFoundException> anEntityNotFoundException(Integer id) {
        return () -> new EntityNotFoundException(msgProvider.getMessage(MSG_NOT_FOUND_ID, id));
    }
}
