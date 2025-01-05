package com.example.MoimMoim.repository;

import com.example.MoimMoim.domain.Role;
import com.example.MoimMoim.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleName roleName);
}
