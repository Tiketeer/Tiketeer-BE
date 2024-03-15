package com.tiketeer.Tiketeer.domain.role.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.tiketeer.Tiketeer.domain.role.Role;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;

@DataJpaTest
class RoleRepositoryTest {
	@Autowired
	private RoleRepository roleRepository;

	@Test
	@DisplayName("롤 저장 > 롤 조회 > 저장한 값과 조회된 값 비교")
	void findByName() {
		Role role = roleRepository.save(new Role(RoleEnum.BUYER));

		Optional<Role> role1 = roleRepository.findByName(role.getName());

		Assertions.assertThat(role1.get().getId()).isEqualTo(role.getId());
	}
}