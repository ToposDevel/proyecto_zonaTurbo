package com.topostechnology.rest.client.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurboOfficeCallDetail {

	private String destination;
	private Integer minutes;
	private String startAt;
	private String stopAt;

}
