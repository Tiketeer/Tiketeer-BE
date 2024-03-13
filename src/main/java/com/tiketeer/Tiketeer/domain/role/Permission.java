package com.tiketeer.Tiketeer.domain.role;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.tiketeer.Tiketeer.domain.role.constant.PermissionEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "permissions")
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Permission {
	@Id
	@UuidGenerator
	@Column(name = "permission_id", nullable = false, updatable = false)
	private UUID id;

	@Enumerated(EnumType.STRING)
	@Column(name = "name", nullable = false)
	private PermissionEnum name;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Builder
	public Permission(PermissionEnum name) {
		this.name = name;
	}
}
