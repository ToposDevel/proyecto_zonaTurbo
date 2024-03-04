package com.topostechnology.rest.client.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcrobitsUserAccountResponse {
	private Long account_id;
	private String cloud_username;
	private Boolean locked;
	
}

