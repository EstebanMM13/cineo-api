package com.estebanmm13.auth_service.repositories;

import com.estebanmm13.auth_service.models.Role;
import com.estebanmm13.auth_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByUsernameIgnoreCase(String username); // 🔹 NUEVO: búsqueda exacta
    Optional<User> findUserByUsernameIgnoreCaseContaining(String username); // contiene
    Optional<User> findUserByEmail(String username);
    Optional<User> findUserByEmailIgnoreCase(String email); // 🔹 NUEVO
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    long countByRole(Role role);
}