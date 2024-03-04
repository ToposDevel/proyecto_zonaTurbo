package com.topostechnology.rest.client.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOfferResponse {
	
    private String msisdn;
    private String effectiveDate;
    private String offeringId;
    private String startEffectiveDate;
    private String expireEffectiveDate;
    private Order order;


}

