package com.topostechnology.repository;

import org.springframework.stereotype.Repository;

import com.topostechnology.domain.ConfirmationToken;

@Repository
public interface ConfirmationTokenRepository extends BaseRepository<ConfirmationToken, Long> {
	ConfirmationToken findByConfirmationTokenAndActive(String confirmationToken, boolean active);
	ConfirmationToken findByUserIdAndActive(Long userId, boolean active);
}
