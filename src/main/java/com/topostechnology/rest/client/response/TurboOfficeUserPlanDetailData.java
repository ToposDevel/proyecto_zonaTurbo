package com.topostechnology.rest.client.response;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurboOfficeUserPlanDetailData {
	private Long turboOfficeUserPlanDetailId;
	private String period;
	private Date activatedAt;
	private Date expireAt;
}
