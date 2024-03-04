package com.topostechnology.rest.client.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendSmsRequest {
	private String number;
	private String message;
	private String usuario;
	private String password;
}

