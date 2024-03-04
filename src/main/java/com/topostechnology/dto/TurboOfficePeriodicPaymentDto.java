package com.topostechnology.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurboOfficePeriodicPaymentDto {
	
	private String planName;
	private String fullUserName;
    private Date expireAt;
    private Long tOfficeUserPlanDetailId;
    private String email;
    private String cellphoneNumber;
    
}
