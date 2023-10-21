package drk.shopamos.rest.controller.mapper;

import drk.shopamos.rest.controller.request.CategoryRequest;
import drk.shopamos.rest.controller.response.CategoryResponse;
import drk.shopamos.rest.model.entity.Category;

import org.mapstruct.Mapper;

@Mapper
public interface CategoryMapper {

    Category map(CategoryRequest categoryRequest);

    CategoryResponse map(Category category);
}
