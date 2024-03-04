package com.topostechnology.webhook.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChargeData {
	private String id;
	private String created_at;
	private String currency;
	private String payment_method;
	private String description;
	private String status;
	private String amount;
	private String paid_at;
	private String fee;
	private String customer_id;
	private String order_id;
}
