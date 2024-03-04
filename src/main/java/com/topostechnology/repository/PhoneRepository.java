package com.topostechnology.repository;

import org.springframework.stereotype.Repository;

import com.topostechnology.domain.Phone;

@Repository
public interface PhoneRepository extends BaseRepository<Phone, Long> {
	
	Phone findByCellphoneNumber(String cellphoneNumber);

}
