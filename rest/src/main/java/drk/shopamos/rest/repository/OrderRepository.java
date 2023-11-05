package drk.shopamos.rest.repository;

import drk.shopamos.rest.model.entity.Order;
import drk.shopamos.rest.model.enumerable.OrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query(
            "SELECT o FROM Order o LEFT JOIN OrderProduct op ON o.id = op.order.id "
                    + "WHERE (:username is NULL OR lower(o.user.email) like CONCAT('%',lower(:username),'%'))"
                    + "  AND (:orderStatus is NULL OR :orderStatus = o.status)"
                    + "  AND (:updatedDateFrom is NULL OR :updatedDateFrom <= o.updatedDate)"
                    + "  AND (:updatedDateTo is NULL OR :updatedDateTo >= o.updatedDate) "
                    + "  AND (:productIdsSize is NULL OR :productIdsSize = 0 OR op.product.id IN :productIds)"
                    + "  GROUP BY o.id "
                    + "  HAVING :productIdsSize is NULL OR :productIdsSize = 0 OR count(op.order.id) = :productIdsSize")
    List<Order> findAllByAttributes(
            String username,
            OrderStatus orderStatus,
            LocalDateTime updatedDateFrom,
            LocalDateTime updatedDateTo,
            List<Integer> productIds,
            Integer productIdsSize);
}
