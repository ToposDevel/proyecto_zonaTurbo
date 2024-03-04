package com.topostechnology.rest.client.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagementServiceRequest {
	
	private String msisdn;
	private String voiceMail;
	private String msisdn_voiceMail;
	private String callForwarding;
	private String msisdn_callForwarding;
	private String unconditionalCallForwarding;
	private String msisdn_unconditionalCallForwarding;
	private String tripartiteCallWaiting;
	private String showPrivateNumbers;
	
}

