package com.tiketeer.Tiketeer.domain.purchase;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;

import com.tiketeer.Tiketeer.domain.member.Member;

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

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "purchases")
@Getter
public class Purchase {
	@Id
	@UuidGenerator
	@Column(name = "purchase_id", nullable = false, updatable = false)
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "owner_id", referencedColumnName = "member_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Member member;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Builder
	public Purchase(Member member) {
		this.member = member;
	}

	public void setMember(Member member) {
		if (this.member != null) {
			this.member.getPurchases().remove(this);
		}
		this.member = member;
		if (member == null) {
			return;
		}
		this.member.getPurchases().add(this);
	}

}
