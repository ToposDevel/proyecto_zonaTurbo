package com.topostechnology.rest.client.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentLinkResponse extends GeneralResponse {
	
	private String paymentLink;

}
