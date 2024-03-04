package com.topostechnology.repository;

import com.topostechnology.domain.OddoUrlOffert;

public interface OddoUrlOffertRepository extends BaseRepository<OddoUrlOffert, Long> {
	
	
	OddoUrlOffert findByofferId(Long offerId);
}
