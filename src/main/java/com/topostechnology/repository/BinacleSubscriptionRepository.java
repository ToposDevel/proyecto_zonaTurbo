package com.topostechnology.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.topostechnology.domain.BinacleSubscription;

public interface BinacleSubscriptionRepository extends JpaRepository<BinacleSubscription, Long> {
}
