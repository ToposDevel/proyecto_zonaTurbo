package com.topostechnology.rest.client.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcrobitsUserAccountRequest {
	
	private String requested_fields;
	private String cloud_username;
}

