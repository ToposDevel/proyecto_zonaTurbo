package com.topostechnology.repository;

import com.topostechnology.domain.OpenpayOrder;

public interface OpenpayOrderRepository extends BaseRepository<OpenpayOrder, Long> {
	
	OpenpayOrder findByOrderId(String  orderId );
}