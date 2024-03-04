package com.topostechnology.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyReportModel {
	
	private String associatedNumber;
	private String imsi;
	private String status;
	private String offerIdd;
	private String offerName;
	private String effDate;
	private String portingStatus;
	private String portingNumber;
	private Date portingAt;
	
}
