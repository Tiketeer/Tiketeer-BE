package com.tiketeer.Tiketeer.domain.member.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.Member;

import jakarta.persistence.LockModeType;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
	Optional<Member> findByEmail(String email);

	@Transactional
	@Lock(value = LockModeType.OPTIMISTIC)
	Optional<Member> findForUpdateByEmail(String email);
}
