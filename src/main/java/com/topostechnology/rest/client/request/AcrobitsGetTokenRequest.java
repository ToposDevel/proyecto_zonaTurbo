package com.topostechnology.rest.client.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcrobitsGetTokenRequest {
	
	private String cloud_id;
	private String user;
	private String password;
	
	public AcrobitsGetTokenRequest(String cloudId, String user, String password ){
		this.cloud_id = cloudId;
		this.user = user;
		this.password = password;
	}

}
