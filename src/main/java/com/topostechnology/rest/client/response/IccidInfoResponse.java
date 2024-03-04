package com.topostechnology.rest.client.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IccidInfoResponse {
	
	private String codigo;
	private String mensaje;
	private String imsi;
	private String msisdn;

}
