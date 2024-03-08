package com.tiketeer.Tiketeer.domain.member;

import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members")
@Getter
public class Member {
	@Id
	@UuidGenerator
	@Column(name = "member_id", nullable = false, updatable = false)
	private UUID id;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "point", nullable = false)
	private int point = 0;

	@Column(name = "enabled", nullable = false)
	private boolean enabled;

	@Column(name = "profile_url")
	private String profileUrl;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", nullable = false)
	private LocalDate createdAt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_login_at")
	private LocalDate lastLoginAt;

	@Builder
	public Member(String email, String password, int point, boolean enabled, String profileUrl) {
		this.email = email;
		this.password = password;
		this.point = point;
		this.enabled = enabled;
		this.profileUrl = profileUrl;
	}
}
