package com.topostechnology.model;

import java.util.List;

import com.topostechnology.rest.client.response.TurboOfficePlanDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurboOfficeActivationModel {

	private String cellphoneNumber;
	private String cellphoneNumberConfirmation;
	private String company;
	private String fullName;
	private String imei;
	private String email;
	private Integer price;
	private String paymentLink;
	private String turboOfficePlanName;
	private List<TurboOfficePlanDto>  turboOfficePlans;

}
