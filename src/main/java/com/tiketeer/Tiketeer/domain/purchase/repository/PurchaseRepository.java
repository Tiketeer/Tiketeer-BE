package com.tiketeer.Tiketeer.domain.purchase.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiketeer.Tiketeer.domain.purchase.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {
}
