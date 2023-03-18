package com.inventory.mgt.sytem.inventory.mgt.system.repository;

import com.inventory.mgt.sytem.inventory.mgt.system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);

    Optional< Boolean> existsByEmail(String email);

    Optional< Boolean> existsByUsername (String username);
    Optional<User> findUserByEmail(String email);

   Optional<User>findByFirstName(String fName);
}
