package com.topostechnology.rest.client.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurboOfficeUserData {
	private String virtualNumber;
	private String associatedNumber;
	private String mobileInstallNumber;
	private String imei;
	private String fullUserName;
	private String email;
	private String lastPaymentAt;
	private String createdAt;
	private String lastExpireAt;
	private String planName;
	private Long planId;
	private String turboOfficeStatus;
	private String acrobitsStatus;
	private String acrobitsLockedStatus;
	private List<TurboOfficeUserPlanDetailData> planDetails;
	private int totalPeridoMinutes;
	private int totalPlanMinutes;
	private int totalExtraRechargeTotalMinutes;
	private int totalConsumedMinutes;
	private int totalAvailableMinutes;
}
