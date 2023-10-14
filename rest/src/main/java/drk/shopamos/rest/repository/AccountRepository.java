package drk.shopamos.rest.repository;

import drk.shopamos.rest.model.entity.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository
        extends JpaRepository<Account, Integer>, QueryByExampleExecutor<Account> {
    Optional<Account> findByEmail(String email);

    Optional<Account> findById(Integer id);

    void deleteById(Integer id);

    boolean existsByEmail(String email);

    boolean existsById(Integer id);

    @Query(
            "SELECT a FROM Account a "
                    + "where (:name is NULL or lower(a.name) like CONCAT('%',lower(:name),'%')) "
                    + "and (:email is NULL or lower(a.email) like CONCAT('%',lower(:email),'%')) "
                    + "and (:isAdmin is NULL or a.isAdmin = :isAdmin) "
                    + "and(:isActive is NULL or a.isActive = :isActive)")
    List<Account> findAllByAttributes(String name, String email, Boolean isAdmin, Boolean isActive);
}
