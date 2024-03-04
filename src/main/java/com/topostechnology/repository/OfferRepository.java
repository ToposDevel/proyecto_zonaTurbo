package com.topostechnology.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.topostechnology.domain.Offer;

@Repository
public interface OfferRepository extends BaseRepository<Offer, Long> {
	
	Offer findByOfferId(Long offerId);
	List<Offer> findAllByOfferId(Long offerId);
	List<Offer> findByCommercialName(String comercialName);
}
