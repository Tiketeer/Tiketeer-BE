package com.tiketeer.Tiketeer.domain.member.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.Otp;

@Repository
public interface OtpRepository extends JpaRepository<Otp, UUID> {
	Optional<Otp> findByMember(Member member);
}
