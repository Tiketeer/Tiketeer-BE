package com.tiketeer.Tiketeer.domain.role.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tiketeer.Tiketeer.domain.role.Role;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;

public interface RoleRepository extends JpaRepository<Role, UUID> {
	Optional<Role> findByName(RoleEnum roleEnum);
}
