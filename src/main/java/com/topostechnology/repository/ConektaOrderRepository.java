package com.topostechnology.repository;

import com.topostechnology.domain.ConektaOrder;

public interface ConektaOrderRepository extends BaseRepository<ConektaOrder, Long> {

	
	ConektaOrder findByConektaOrderId(String  orderId );

}