package com.topostechnology.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BalanceModel {
	
	private Long offerId;
	private String offerName;
	private String commercialName;
	private String effectiveDate;
	private String expireDate;
	
    private Integer bytesTotalAmt;
    private Double bytesConsumedAmt;
    private Double bytesUnusedAmt;
    
    private Integer smsTotalAmt;
    private Integer smsConsumedAmt;
    private Integer smsUnusedAmt;
    
    private Integer minutesTotalAmt;
    private Integer minutesConsumedAmt;
    private Integer minutesUnusedAmt;

}
