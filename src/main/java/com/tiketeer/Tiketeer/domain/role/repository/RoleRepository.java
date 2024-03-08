package com.tiketeer.Tiketeer.domain.role.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tiketeer.Tiketeer.domain.role.Role;

public interface RoleRepository extends JpaRepository<Role, UUID> {
}
