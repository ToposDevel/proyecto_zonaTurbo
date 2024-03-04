package com.topostechnology.model;

import java.util.List;

import com.topostechnology.rest.client.response.TurboOfficePlanDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurboOfficeRechargeModel {

	private String virtualNumber;
	private String email;
	private List<TurboOfficePlanDto>  turboOfficeRechargeOptions;
	private TurboOfficePlanDto  turboOfficeRechargeSelected;
	private String paymentLink;
//	private String  turboOfficeRechargeSelectedId;
	private Integer price;
	private String turboOfficePlanName;

}
