package com.tiketeer.Tiketeer.domain.member;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "otps")
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Otp {
	@Id
	@UuidGenerator
	@Column(name = "password", nullable = false, updatable = false)
	private UUID password;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "expired_at", nullable = false)
	private LocalDateTime expiredAt;

	@OneToOne(fetch = FetchType.LAZY)
	@Setter
	@JoinColumn(name = "member_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Member member;

	@Builder
	public Otp(LocalDateTime expiredAt, Member member) {
		this.expiredAt = expiredAt;
		this.member = member;
	}

	public static final int OTP_VALID_MINUTE = 30;
}
