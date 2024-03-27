package com.tiketeer.Tiketeer.domain.ticketing.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.GetMemberTicketingSalesResultDto;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;

@Repository
public interface TicketingRepository extends JpaRepository<Ticketing, UUID> {

	@Query(value = """
		select new com.tiketeer.Tiketeer.domain.member.usecase.dto.GetMemberTicketingSalesResultDto(
			t.id, t.title, t.description, t.location, t.eventTime, t.saleStart, t.saleEnd,
			(select count(tk) from Ticket tk where tk.ticketing = t),
			(select count(tk) from Ticket tk where tk.ticketing = t and tk.purchase is null ),
			t.createdAt, t.category, t.runningMinutes, t.price
		)
		from Ticketing t
		where t.member.email = :email
		"""
	)
	List<GetMemberTicketingSalesResultDto> findTicketingWithTicketStock(@Param("email") String email);

	List<Ticketing> findAllByMember(Member member);
}
