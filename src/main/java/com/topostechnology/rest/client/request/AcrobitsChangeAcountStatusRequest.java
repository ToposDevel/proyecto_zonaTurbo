package com.topostechnology.rest.client.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcrobitsChangeAcountStatusRequest {
	
	String cloud_username;
	String new_status;

}
