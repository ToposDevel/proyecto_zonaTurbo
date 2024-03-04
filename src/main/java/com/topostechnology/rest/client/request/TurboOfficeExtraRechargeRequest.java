package com.topostechnology.rest.client.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurboOfficeExtraRechargeRequest {
	
	@NotNull(message="must not be null")
	@Size(min = 10, max = 10  , message =":the minimum length is 10 characters and maximum  10")
	private String virtualNumber;
	
	@NotNull(message="must not be null")
	@NotEmpty(message="is required")
	@Size(max = 50  , message =":the maximum length  50")
	private String email;
	
	private String paymentLink;
	
	@NotNull(message="must not be null")
	@NotEmpty(message="is required")
	private String company;
	
	@NotNull(message="must not be null")
	@NotEmpty(message="is required")
	private String turboOfficePlanName;
	
	private Long tOfficeUserPlanDetailId;

}
