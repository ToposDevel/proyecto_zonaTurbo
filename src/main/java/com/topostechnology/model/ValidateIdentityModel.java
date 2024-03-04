package com.topostechnology.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateIdentityModel {
	
	private String virtualNumber;
	private String virtualNumberConfirmation;
	private String associatedNumber;
	private String associatedNumberConfirmation;
	private String mobileInstallNumber;
	private String mobileInstallNumberConfirmation;
	private String validationCode;
    private boolean sentCode;
    private boolean validated;
    

}
