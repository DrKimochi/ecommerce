package drk.shopamos.rest.repository;

import drk.shopamos.rest.model.entity.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findById(String id);

    void deleteById(String id);
}
