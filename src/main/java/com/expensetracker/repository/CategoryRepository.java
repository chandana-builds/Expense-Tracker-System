package com.expensetracker.repository;

import com.expensetracker.model.Category;
import com.expensetracker.model.TransactionType;
import com.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.user = :user OR c.isDefault = true ORDER BY c.name ASC")
    List<Category> findAllForUser(@Param("user") User user);

    @Query("SELECT c FROM Category c WHERE (c.user = :user OR c.isDefault = true) AND c.type = :type ORDER BY c.name ASC")
    List<Category> findForUserAndType(@Param("user") User user, @Param("type") TransactionType type);

    Optional<Category> findByIdAndUser(Long id, User user);
    
    boolean existsByNameIgnoreCaseAndUser(String name, User user);
}
