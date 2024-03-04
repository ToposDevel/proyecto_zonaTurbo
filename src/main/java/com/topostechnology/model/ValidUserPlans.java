package com.topostechnology.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ValidUserPlans {
	private String errorMessage;
	private boolean belongsToTurbored;
	private List<PlanModel> planList;
	private String cellphoneNumber; 
	private String altanOperation;
	private String superOfferType;

}
