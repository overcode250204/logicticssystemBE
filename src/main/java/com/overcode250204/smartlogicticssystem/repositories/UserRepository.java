package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByRole_RoleIdInAndIsActiveTrue(List<Integer> roleIds);

    @Query("SELECT u FROM User u WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR u.phone LIKE CONCAT('%', :keyword, '%')) " +
            "AND (:roleId IS NULL OR u.role.roleId = :roleId) " +
            "AND (:isActive IS NULL OR u.isActive = :isActive)")
    List<User> searchWithConditions(
            @Param("keyword") String keyword,
            @Param("roleId") Integer roleId,
            @Param("isActive") Boolean isActive
    );
    Optional<User> findByPhone(String phone);
}
