package drk.shopamos.rest.repository;

import drk.shopamos.rest.model.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query(
            "SELECT p FROM Product p "
                    + "WHERE (:categoryId is NULL OR :categoryId = p.category.id) "
                    + "  AND (:name is NULL OR lower(p.name) like CONCAT('%',lower(:name),'%')) "
                    + "  AND (:description is NULL OR lower(p.description) like CONCAT('%',lower(:description),'%')) "
                    + "  AND (:priceFrom is NULL OR p.price >= :priceFrom)"
                    + "  AND (:priceTo is NULL OR p.price <= :priceTo)"
                    + "  AND (:isActive is NULL OR p.isActive = :isActive)")
    List<Product> findAllByAttributes(
            String categoryId,
            String name,
            String description,
            BigDecimal priceFrom,
            BigDecimal priceTo,
            Boolean isActive);

    Optional<Product> findById(Integer id);
}
