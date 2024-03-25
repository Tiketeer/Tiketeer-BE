package com.tiketeer.Tiketeer.domain.ticketing;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.tiketeer.Tiketeer.domain.member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
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
import lombok.ToString;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ticketings")
@EntityListeners(AuditingEntityListener.class)
@Getter
@ToString
public class Ticketing {
	@Id
	@UuidGenerator
	@Column(name = "ticketing_id", nullable = false, updatable = false)
	private UUID id;

	@Setter
	@Column(name = "price", nullable = false)
	private long price;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", referencedColumnName = "member_id", foreignKey = @ForeignKey(value =
		ConstraintMode.NO_CONSTRAINT))
	private Member member;

	@Setter
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Setter
	@Column(name = "title", nullable = false)
	private String title;

	@Setter
	@Column(name = "location", nullable = false)
	private String location;

	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "event_time", nullable = false)
	private LocalDateTime eventTime;

	@Setter
	@Column(name = "category")
	private String category;

	@Setter
	@Column(name = "running_minutes", nullable = false)
	private int runningMinutes;

	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sale_start", nullable = false)
	private LocalDateTime saleStart;

	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sale_end", nullable = false)
	private LocalDateTime saleEnd;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Builder
	public Ticketing(
		long price, Member member,
		String description, String title,
		String location, LocalDateTime eventTime,
		String category, int runningMinutes,
		LocalDateTime saleStart, LocalDateTime saleEnd
	) {
		this.price = price;
		this.member = member;
		this.description = description;
		this.title = title;
		this.location = location;
		this.eventTime = eventTime;
		this.category = category;
		this.runningMinutes = runningMinutes;
		this.saleStart = saleStart;
		this.saleEnd = saleEnd;
	}

}
