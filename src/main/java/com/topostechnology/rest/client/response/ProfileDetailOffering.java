package com.topostechnology.rest.client.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDetailOffering {
	private String offeringId;
	private String purchaseSecuence;
	private String initialAmt;
	private String unusedAmt;
	private String effectiveDate;
	private String expireDate;
}

