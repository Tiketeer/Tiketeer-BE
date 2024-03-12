package com.tiketeer.Tiketeer.domain.ticket;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@Table(name = "tickets")
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Ticket {
	@Id
	@UuidGenerator
	@Column(name = "ticket_id", nullable = false, updatable = false)
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "purchase_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Purchase purchase;

	@ManyToOne
	@JoinColumn(name = "ticketing_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Ticketing ticketing;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Builder
	public Ticket(Purchase purchase, Ticketing ticketing) {
		this.purchase = purchase;
		this.ticketing = ticketing;
	}

}
