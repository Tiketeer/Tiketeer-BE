package com.tiketeer.Tiketeer.domain.purchase.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.service.dto.GetMemberPurchasesResultDto;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {
	@Query(
		"SELECT new com.tiketeer.Tiketeer.domain.member.service.dto.GetMemberPurchasesResultDto(p.id, tg.id, tg.title, tg.location, tg.eventTime, tg.saleStart, tg.saleEnd, p.createdAt, tg.category, tg.price, count(*)) "
			+ "FROM Purchase p LEFT JOIN Ticket t ON p = t.purchase LEFT JOIN Ticketing tg ON t.ticketing = tg "
			+ "WHERE p.member = :member GROUP BY p, tg ORDER BY p.createdAt"
	)
	List<GetMemberPurchasesResultDto> findWithTicketingByMember(@Param("member") Member member);
}
