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
@Table(name = "otps")
@Getter
public class Otp {
	@Id
	@UuidGenerator
	@Column(name = "password", nullable = false, updatable = false)
	private UUID password;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", nullable = false)
	private LocalDate createdAt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "expired_at", nullable = false)
	private LocalDate expiredAt;

	@Builder
	public Otp(LocalDate expiredAt) {
		this.expiredAt = expiredAt;
	}
}
