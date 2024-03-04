package com.topostechnology.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurboOfficeUserModel {
	private String virtualNumber;
	private String associatedNumber;
	private String fullName;
	private String email;
	private String turboOfficePlanName;
	private String createdAt;
	private String lastPaymentAt;
	private String expiredAt;
    private String planName;
    private Long planId;
    private String turboOfficeStatus;
    private String acrobitsStatus;
	private List<TurboOfficeUserPlanDetail> planDetails;
    private int totalPeridoMinutes;
    private int totalPlanMinutes;
    private int totalExtraRechargeTotalMinutes;
    private int totalConsumedMinutes;
    private int totalAvailableMinutes;
    private Boolean acrobitsLockedStatus;
}
