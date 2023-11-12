package drk.shopamos.rest.service;

import static drk.shopamos.rest.config.MessageProvider.MSG_NOT_FOUND_CATEGORY;

import drk.shopamos.rest.config.MessageProvider;
import drk.shopamos.rest.model.entity.Product;
import drk.shopamos.rest.repository.CategoryRepository;
import drk.shopamos.rest.repository.ProductRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;

@Service
public class ProductService extends BaseService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public ProductService(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            MessageProvider messageProvider,
            Clock clock) {
        super(messageProvider, clock);
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product createProduct(String categoryId, Product product) {
        return saveProduct(categoryId, product);
    }

    public Product updateProduct(String newCategoryId, Product productForUpdate) {
        Integer id = productForUpdate.getId();
        if (productRepository.findById(id).isEmpty()) {
            throw anEntityNotFoundException(id).get();
        }
        return saveProduct(newCategoryId, productForUpdate);
    }

    public void deleteProduct(Integer id) {
        Product product = getProduct(id);
        productRepository.delete(product);
    }

    public Product getActiveProduct(Integer id) {
        Product product = getProduct(id);
        if (!product.isActive()) {
            throw anEntityNotFoundException(id).get();
        }
        return product;
    }

    public Product getProduct(Integer id) {
        return productRepository.findById(id).orElseThrow(anEntityNotFoundException(id));
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
            throw anEntityNotFoundException(MSG_NOT_FOUND_CATEGORY, categoryId).get();
        }
        product.setCategory(categoryRepository.getReferenceById(categoryId));
        return productRepository.save(product);
    }
}
