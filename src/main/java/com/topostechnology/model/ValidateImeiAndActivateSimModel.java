package com.topostechnology.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateImeiAndActivateSimModel extends ActivateSimModel {
	
	private String cellphoneNumber;
	private String fullName;
	private String email;
	private String imei;
}
