package drk.shopamos.rest.controller.mapper;

import drk.shopamos.rest.controller.request.ProductRequest;
import drk.shopamos.rest.controller.response.ProductResponse;
import drk.shopamos.rest.model.entity.Product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProductMapper {

    @Mapping(target = "category", ignore = true)
    Product map(ProductRequest productRequest, Integer id);

    @Mapping(target = "categoryId", source = "product.category.id")
    ProductResponse map(Product product);

    default Product map(ProductRequest productRequest) {
        return map(productRequest, null);
    }
}
