package com.topostechnology.rest.client.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoordinatesResponse  extends GeneralResponse {
	
	public double lat;
	public double lng;
	private String formattedAddress;
	
	public CoordinatesResponse() {
		
	}
	
	
	public CoordinatesResponse(int code, String message) {
		super(code, message);
	}

}
