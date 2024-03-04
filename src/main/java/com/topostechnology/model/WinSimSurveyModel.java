package com.topostechnology.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WinSimSurveyModel {
	
	private String fullName;
	private String email;
	private String cellphoneNumber;
	private String imei;

	private String fullNameRecomendation1;
	private String emailRecomendation1;
	private String cellphoneNumberRecomendation1;
	
	private String fullNameRecomendation2;
	private String emailRecomendation2;
	private String cellphoneNumberRecomendation2;
	
	private String fullNameRecomendation3;
	private String emailRecomendation3;
	private String cellphoneNumberRecomendation3;
}
