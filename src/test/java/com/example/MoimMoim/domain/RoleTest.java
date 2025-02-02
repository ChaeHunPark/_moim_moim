package com.example.MoimMoim.domain;

import com.example.MoimMoim.enums.RoleName;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {
    @Test
    @DisplayName("Role 객체 생성 시 ROLE_USER가 설정되어야 한다.")
    void testRoleCreation() {

        //given
        Role role = new Role();
        role.setRoleName(RoleName.ROLE_USER);

        //then
        Assertions.assertThat(role.getRoleName())
                .isEqualTo(RoleName.ROLE_USER);
    }

    @Test
    @DisplayName("Role 객체 생성 시 ROLE_ADMIN이 설정되어야 한다.")
    void testRoleEnum() {
        // Given
        Role role = new Role();
        role.setRoleName(RoleName.ROLE_ADMIN);

        // Then
        Assertions.assertThat(role.getRoleName())
                .isEqualTo(RoleName.ROLE_ADMIN);
    }

    @Test
    @DisplayName("Role 생성자를 통해 ROLE_USER 역할이 설정된 객체가 생성되어야 한다.")
    void testRoleWithConstructor() {
        // Given
        Role role = new Role(1L, RoleName.ROLE_USER);

        // Then
        Assertions.assertThat(role.getRoleName())
                .isEqualTo(RoleName.ROLE_USER);

        // And
        Assertions.assertThat(role.getId())
                .isNotNull();
    }
}