package com.topostechnology.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompatibilityModel {
	
	private String imei;
	private boolean voiceApp;
	private boolean compatible;
	private boolean noCompatible;
	private String compatibleOptUrl;
	private String noCompatibleOptUrl;
	private String vozappUrl;
	private String googleplayUrl;
	private String message;

}
