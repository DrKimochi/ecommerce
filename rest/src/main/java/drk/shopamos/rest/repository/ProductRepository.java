package drk.shopamos.rest.repository;

import drk.shopamos.rest.model.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {}
