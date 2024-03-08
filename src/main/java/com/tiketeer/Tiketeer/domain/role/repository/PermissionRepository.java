package com.tiketeer.Tiketeer.domain.role.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tiketeer.Tiketeer.domain.role.Permission;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
}
