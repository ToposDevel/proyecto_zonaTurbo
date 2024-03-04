package com.topostechnology.rest.client.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoPortingRequest {
	
	private String msisdnTransitory;
	private String msisdnPorted;
	private String imsi;
	private String approvedDateABD;
	private String dida;
	private String rida;
	private String dcr;
	private String rcr;
	private String autoScriptReg;
	
}
