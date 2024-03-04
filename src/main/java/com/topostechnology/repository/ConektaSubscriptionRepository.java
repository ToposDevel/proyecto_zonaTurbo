package com.topostechnology.repository;

import com.topostechnology.domain.ConektaSubscription;

public interface ConektaSubscriptionRepository extends BaseRepository<ConektaSubscription, Long> {
	
	ConektaSubscription findByConektaSubscriptionId(String  subscriptionId );

}
