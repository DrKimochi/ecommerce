package drk.shopamos.rest.controller;

import drk.shopamos.rest.controller.mapper.ProductMapper;
import drk.shopamos.rest.controller.request.ProductRequest;
import drk.shopamos.rest.controller.response.ProductResponse;
import drk.shopamos.rest.model.entity.Account;
import drk.shopamos.rest.model.entity.Product;
import drk.shopamos.rest.service.ProductService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/products")
public class ProductController {
    private final ProductService service;
    private final ProductMapper mapper;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest productRequest) {

        Product product =
                service.createProduct(productRequest.getCategoryId(), mapper.map(productRequest));

        return ResponseEntity.ok(mapper.map(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable(name = "id") Integer id,
            @Valid @RequestBody ProductRequest productRequest) {

        Product product =
                service.updateProduct(productRequest.getCategoryId(), mapper.map(productRequest, id));

        return ResponseEntity.ok(mapper.map(product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable(name = "id") Integer id) {

        service.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ADMIN')")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable(name = "id") Integer id) {
        Product product =
                getPrincipal().isAdmin() ? service.getProduct(id) : service.getActiveProduct(id);

        return ResponseEntity.ok(mapper.map(product));
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('CUSTOMER','ADMIN')")
    public ResponseEntity<List<ProductResponse>> getProducts(
            @RequestParam(name = "categoryId", required = false) String categoryId,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "priceFrom", required = false) BigDecimal priceFrom,
            @RequestParam(name = "priceTo", required = false) BigDecimal priceTo,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {

        isActive = getPrincipal().isAdmin() ? isActive : Boolean.TRUE;

        List<Product> foundProducts =
                service.getProducts(categoryId, name, description, priceFrom, priceTo, isActive);
        List<ProductResponse> response = foundProducts.stream().map(mapper::map).toList();
        return ResponseEntity.ok(response);
    }

    private Account getPrincipal() {
        return (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
