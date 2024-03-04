package com.topostechnology.rest.client.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendEmailRequest {

	private String from;

	private String cellphoneNumber;

	private String userName;

	private String subject;

	private String message;

	private String param;

	private List<AttachFile> attachFiles;
	
	private String[] to;

}
