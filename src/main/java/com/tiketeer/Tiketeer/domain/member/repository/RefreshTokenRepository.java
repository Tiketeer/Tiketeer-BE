package com.tiketeer.Tiketeer.domain.member.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tiketeer.Tiketeer.domain.member.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
}
