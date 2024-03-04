package com.topostechnology.rest.client.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurboOfficeUserPlanDetailResponse  extends GeneralResponse {
	
	private String planName;
	private String fullUserName;
    private String expireAt;
    private Long tOfficeUserPlanDetailId;
    
}
