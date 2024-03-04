package com.topostechnology.webhook.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionEvent extends WebhookBaseObject {
	
	private String id;
	private String status;
	private String object;
	private String charge_id;
	private String created_at;
	private String subscription_start;
	private String canceled_at;
	private String paused_at;
	private String billing_cycle_start;
	private String billing_cycle_end;
	private String trial_start;
	private String trial_end;
	private String plan_id;
	private String last_billing_cycle_order_id;
	private String customer_id;
	private String customer_custom_reference;
	private String card_id;

}
