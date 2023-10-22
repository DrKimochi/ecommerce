package drk.shopamos.rest.repository;

import drk.shopamos.rest.model.entity.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findById(String id);

    void deleteById(String id);

    @Query(
            "SELECT c FROM Category c "
                    + "where (:name is NULL or lower(c.name) like CONCAT('%',lower(:name),'%')) "
                    + "and (:description is NULL or lower(c.description) like CONCAT('%',lower(:description),'%')) ")
    List<Category> findAllByAttributes(String name, String description);
}
