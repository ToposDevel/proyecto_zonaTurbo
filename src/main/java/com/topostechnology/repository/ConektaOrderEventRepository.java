package com.topostechnology.repository;

import java.util.Date;
import java.util.List;

import com.topostechnology.domain.ConektaOrderEvent;

public interface ConektaOrderEventRepository extends BaseRepository<ConektaOrderEvent, Long> {
	ConektaOrderEvent findByConektaChargeId(String  conektaEventId );
	
	List<ConektaOrderEvent> findByCreatedAtBetweenAndEventType(Date initDate, Date endDate, String eventType);
}
