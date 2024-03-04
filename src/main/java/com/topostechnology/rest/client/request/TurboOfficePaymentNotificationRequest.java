package com.topostechnology.rest.client.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurboOfficePaymentNotificationRequest {
	
	@NotNull(message="must not be null")
	@NotEmpty(message="is required")
	@Size(min = 10, max = 10  , message =":the minimum length is 10 characters and maximum  10")
	private String cellphoneNumber;
	
	
	@NotNull(message="must not be null")
	@NotEmpty(message="is required")
	@Size(min = 1, max = 20  , message =":the minimum length is 1 characters and maximum  20")
	private String turboOfficePlanId;

}
