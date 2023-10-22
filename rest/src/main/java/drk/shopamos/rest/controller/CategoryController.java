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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable(name = "id") String id) {
        service.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER','ADMIN')")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable(name = "id") String id) {
        Category category = service.getCategory(id);
        return ResponseEntity.ok(mapper.map(category));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('CUSTOMER','ADMIN')")
    public ResponseEntity<List<CategoryResponse>> getCategories(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "description", required = false) String description) {
        List<Category> foundCategories = service.getCategories(name, description);
        List<CategoryResponse> response = foundCategories.stream().map(mapper::map).toList();
        return ResponseEntity.ok(response);
    }
}
