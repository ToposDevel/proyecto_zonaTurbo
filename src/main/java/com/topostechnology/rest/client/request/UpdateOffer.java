package com.topostechnology.rest.client.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOffer {
	
    private String offeringId;
    private String address;
    private String startEffectiveDate;
    private String expireEffectiveDate;
    private String scheduleDate;

}
