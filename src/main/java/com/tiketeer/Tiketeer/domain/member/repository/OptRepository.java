package com.tiketeer.Tiketeer.domain.member.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tiketeer.Tiketeer.domain.member.Otp;

public interface OptRepository extends JpaRepository<Otp, UUID> {
}
