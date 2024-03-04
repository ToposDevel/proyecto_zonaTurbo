package com.topostechnology.rest.client.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurboOfficeRechargeDto  {
	
	private String turboOfficePlanId;
	private String name;
	private Integer price;
	private Integer minutes;
	private Integer effectiveDays;
	
}
