package com.tiketeer.Tiketeer.domain.member.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiketeer.Tiketeer.domain.member.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
}
