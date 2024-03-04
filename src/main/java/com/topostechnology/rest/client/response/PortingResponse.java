package com.topostechnology.rest.client.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortingResponse  extends GeneralResponse {
	
	private String cellphoneNumber;
	private String status;
	private String portingMessage;
	private String portingDate;
	private String folioid;
	private String portid;
	private String userName;
	private String iccid;
	
}
