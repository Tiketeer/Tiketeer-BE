package com.tiketeer.Tiketeer.domain.member;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.tiketeer.Tiketeer.domain.role.Role;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members")
@Getter
public class Member {
	@Id
	@UuidGenerator
	@Column(name = "member_id", nullable = false, updatable = false)
	private UUID id;

	@Setter
	@Column(name = "email", nullable = false)
	private String email;

	@Setter
	@Column(name = "password", nullable = false)
	private String password;

	@Setter
	@Column(name = "point", nullable = false)
	private long point = 0L;

	@Setter
	@Column(name = "enabled", nullable = false)
	private boolean enabled = false;

	@Setter
	@Column(name = "profile_url")
	private String profileUrl;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_login_at")
	private LocalDateTime lastLoginAt;

	@Setter
	@ManyToOne
	@JoinColumn(name = "role_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Role role;

	@Builder
	public Member(String email, String password, long point, boolean enabled, String profileUrl, Role role) {
		this.email = email;
		this.password = password;
		this.point = point;
		this.enabled = enabled;
		this.profileUrl = profileUrl;
		this.role = role;
	}
}
