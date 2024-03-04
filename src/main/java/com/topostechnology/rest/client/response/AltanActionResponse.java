package com.topostechnology.rest.client.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AltanActionResponse {
	
	private String altanOrderId;
	private String jsonRequest;
	private String jsonResponse;
	private String status;
	private String url;

}
