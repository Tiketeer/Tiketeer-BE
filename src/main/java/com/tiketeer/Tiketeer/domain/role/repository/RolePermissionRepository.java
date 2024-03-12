package com.tiketeer.Tiketeer.domain.role.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tiketeer.Tiketeer.domain.role.RolePermission;

public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {
}
