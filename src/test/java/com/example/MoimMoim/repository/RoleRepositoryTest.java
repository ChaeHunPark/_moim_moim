package com.example.MoimMoim.repository;

import com.example.MoimMoim.domain.Role;
import com.example.MoimMoim.enums.RoleName;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("h2") // H2 데이터베이스를 사용하도록 설정
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("ROLE 저장 및 조회 테스트")
    void findByRoleName() {

        // given
        Role roleUser = new Role();
        Role roleAdmin = new Role();
        roleUser.setRoleName(RoleName.ROLE_USER);
        roleAdmin.setRoleName(RoleName.ROLE_ADMIN);

        // when
        roleRepository.saveAll(List.of(roleUser, roleAdmin));
        List<Role> all = roleRepository.findAll();

        // then
        assertThat(all).isNotNull();
        assertThat(all).containsAll(List.of(roleUser, roleAdmin));

    }

    @Test
    @DisplayName("ROLE 잘못된 저장 - 오류 발생")
    void testSaveRoleWithInvalidData_throwsError() {
        // given
        Role role = new Role();

        assertThatThrownBy(() ->
                roleRepository.save(role) // 빈객체 저장 제약조건 위반 exception 발생
                ).isInstanceOf(ConstraintViolationException.class);
    }
}