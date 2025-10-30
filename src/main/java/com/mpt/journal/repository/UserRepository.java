package com.mpt.journal.repository;

import com.mpt.journal.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Только неудалённые
    Page<User> findByIsDeletedFalse(Pageable pageable);

    // Поиск по username или email (игнорируя удалённых)
    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> searchByUsernameOrEmail(@Param("search") String search, Pageable pageable);

    Optional<User> findByUsernameAndIsDeletedFalse(String username);
    Optional<User> findByEmailAndIsDeletedFalse(String email);
}