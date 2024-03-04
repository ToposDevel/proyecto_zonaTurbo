package com.topostechnology.webhook.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderEvent extends WebhookBaseObject {
	private String livemode;
	private Integer amount;
	private String currency;
	private String payment_status;
	private Integer amount_refunded;
	private CustomerInfo customer_info;
//	private String shipping_contact;
	private String id;
	private Integer created_at;
//	private ChargeEvent charges;

	
}