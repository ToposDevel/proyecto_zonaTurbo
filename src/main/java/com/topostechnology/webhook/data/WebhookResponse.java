package com.topostechnology.webhook.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebhookResponse {
	
    private String id;
    private String object;
    private Boolean livemode;
    private Integer created_at;
    private String type;
    private WebhookData data;
    private String webhook_status;
    private Object webhook_logs;
}
