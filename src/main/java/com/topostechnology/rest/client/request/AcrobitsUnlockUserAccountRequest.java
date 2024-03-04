package com.topostechnology.rest.client.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcrobitsUnlockUserAccountRequest {
	
	private AcrobitsUserLocked updated_data;
	private Long account_id;
}
