package com.topostechnology.webhook.data;

import lombok.Getter;
import lombok.Setter;
import mx.openpay.client.Transaction;

@Getter
@Setter
public class OpenpayEventData {
	
	private String type;
	private String event_date;
	private Transaction transaction;
	
}
