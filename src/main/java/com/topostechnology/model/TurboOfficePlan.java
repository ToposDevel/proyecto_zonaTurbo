package com.topostechnology.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurboOfficePlan  {
	
	private String turboOfficePlanId;
	private String name;
	private Integer price;
	private Integer minutes;
	private Integer effectiveDays;
	
}
