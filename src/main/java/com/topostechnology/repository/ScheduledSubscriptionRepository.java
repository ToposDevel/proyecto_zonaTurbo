package com.topostechnology.repository;

import java.util.Date;
import java.util.List;

import com.topostechnology.domain.ScheduledSubscription;

public interface ScheduledSubscriptionRepository extends BaseRepository<ScheduledSubscription, Long> {
	
//	List<ScheduledSubscription> findByScheduledAt(Date initDate, Date endDate);
	
	List<ScheduledSubscription> findByScheduledAtBetweenAndInternalStatus(Date initDate, Date endDate, String status);
	
}
