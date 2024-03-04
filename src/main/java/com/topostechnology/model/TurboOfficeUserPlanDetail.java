package com.topostechnology.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurboOfficeUserPlanDetail {
	private Long turboOfficeUserPlanDetailId;
	private String period;
	private Date activatedAt;
	private Date expireAt;
}
