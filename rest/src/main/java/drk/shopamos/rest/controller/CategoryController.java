package drk.shopamos.rest.controller;

import drk.shopamos.rest.controller.group.Create;
import drk.shopamos.rest.controller.group.Update;
import drk.shopamos.rest.controller.mapper.CategoryMapper;
import drk.shopamos.rest.controller.request.CategoryRequest;
import drk.shopamos.rest.controller.response.CategoryResponse;
import drk.shopamos.rest.model.entity.Category;
import drk.shopamos.rest.service.CategoryService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/categories")
public class CategoryController {
    private final CategoryService service;
    private final CategoryMapper mapper;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(
            @Validated(Create.class) @RequestBody CategoryRequest categoryRequest) {
        Category category = service.createCategory(mapper.map(categoryRequest));
        return ResponseEntity.ok(mapper.map(category));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable(name = "id") String id,
            @Validated(Update.class) @RequestBody CategoryRequest categoryRequest) {
        categoryRequest.setId(id);
        Category category = service.updateCategory(mapper.map(categoryRequest));
        return ResponseEntity.ok(mapper.map(category));
    }
}
