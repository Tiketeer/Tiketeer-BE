package com.tiketeer.Tiketeer.domain.role;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "role_permissions")
@Getter
public class RolePermission {
	@Id
	@UuidGenerator
	@Column(name = "role_permission_id", nullable = false, updatable = false)
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "role_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Role role;

	@ManyToOne
	@JoinColumn(name = "permission_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Permission permission;

	public void setRole(Role role) {
		if (this.role != null) {
			this.role.getRolePermission().remove(this);
		}
		this.role = role;
		if (role == null) {
			return;
		}
		this.role.getRolePermission().add(this);
	}
}
