package com.topostechnology.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FUConsumptionDetailModel {
	
	private Long cdrId;
	private Date date;
	private String offerName;
	private String  type;
	private Long quantity;
	private String measurementUnit;
	
}