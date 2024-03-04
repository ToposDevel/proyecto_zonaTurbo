package com.topostechnology.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.topostechnology.domain.ConektaSubscriptionEvent;

@Repository
public interface ConektaSubscriptionEventRepository extends BaseRepository<ConektaSubscriptionEvent, Long> {


//	List<ConektaSubscriptionDetail> findByCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(Date date);
	
//	List<ConektaSubscriptionEvent> findByCreatedAtBetween(Date initDate, Date endDate);
	
	@Query(value = "SELECT * from  conekta_subscription_event where event_type= ?1 and conekta_subscription_id = ?2",nativeQuery = true)
	List<ConektaSubscriptionEvent> findByConektaSubscriptionEventAndEventType(String eventType, Long id);
	
	List<ConektaSubscriptionEvent> findByCreatedAtBetweenAndEventType(Date initDate, Date endDate, String eventType);

}
