package com.tiketeer.Tiketeer.testhelper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.repository.OtpRepository;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.role.Permission;
import com.tiketeer.Tiketeer.domain.role.Role;
import com.tiketeer.Tiketeer.domain.role.RolePermission;
import com.tiketeer.Tiketeer.domain.role.constant.PermissionEnum;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.repository.PermissionRepository;
import com.tiketeer.Tiketeer.domain.role.repository.RolePermissionRepository;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;

@TestComponent
public class TestHelper {
	private final PermissionRepository permissionRepository;
	private final RoleRepository roleRepository;
	private final RolePermissionRepository rolePermissionRepository;
	private final MemberRepository memberRepository;
	private final OtpRepository otpRepository;
	private final PurchaseRepository purchaseRepository;
	private final TicketRepository ticketRepository;
	private final TicketingRepository ticketingRepository;

	@Autowired
	public TestHelper(
		PermissionRepository permissionRepository,
		RoleRepository roleRepository,
		RolePermissionRepository rolePermissionRepository,
		MemberRepository memberRepository,
		OtpRepository otpRepository,
		PurchaseRepository purchaseRepository,
		TicketRepository ticketRepository,
		TicketingRepository ticketingRepository
	) {
		this.permissionRepository = permissionRepository;
		this.roleRepository = roleRepository;
		this.rolePermissionRepository = rolePermissionRepository;
		this.memberRepository = memberRepository;
		this.otpRepository = otpRepository;
		this.purchaseRepository = purchaseRepository;
		this.ticketRepository = ticketRepository;
		this.ticketingRepository = ticketingRepository;
	}

	@Transactional
	public void initDB() {
		var readPermit = permissionRepository.save(Permission.builder().name(PermissionEnum.TICKETING_READ).build());
		var writePermit = permissionRepository.save(Permission.builder().name(PermissionEnum.TICKETING_WRITE).build());
		var buyerRole = roleRepository.save(Role.builder().name(RoleEnum.BUYER).build());
		var sellerRole = roleRepository.save(Role.builder().name(RoleEnum.SELLER).build());

		// Buyer RolePermission
		var buyerRolePermission = RolePermission.builder().role(buyerRole).permission(readPermit).build();
		rolePermissionRepository.save(buyerRolePermission);

		// Seller RolePermission
		var sellerRolePermission1 = RolePermission.builder().role(sellerRole).permission(readPermit).build();
		var sellerRolePermission2 = RolePermission.builder().role(sellerRole).permission(writePermit).build();
		rolePermissionRepository.saveAll(List.of(sellerRolePermission1, sellerRolePermission2));

	}

	@Transactional
	public void cleanDB() {
		List.of(
			ticketRepository,
			purchaseRepository,
			ticketingRepository,
			otpRepository,
			memberRepository,
			rolePermissionRepository,
			roleRepository,
			permissionRepository
		).forEach(JpaRepository::deleteAll);
	}
}
