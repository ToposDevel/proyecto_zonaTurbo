package com.topostechnology.rest.client.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneralResponse {
	
	private int code;
	private String message;
	
	public GeneralResponse() {
		
	}
	
	public GeneralResponse(int code, String message) {
		this.code = code;
		this.message = message;
	}

}
