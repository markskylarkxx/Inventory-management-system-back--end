package com.inventory.mgt.sytem.inventory.mgt.system.repository;

import com.inventory.mgt.sytem.inventory.mgt.system.model.Role;
import com.inventory.mgt.sytem.inventory.mgt.system.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(RoleName roleName);
}
