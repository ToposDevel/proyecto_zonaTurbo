package com.topostechnology.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentModel {

	private String cellphoneNumber;
	private String cellphoneNumberConfirmation;
	private String imei;
	private String imeiConfirmation;
	private String planSelectedId;
	private String planSelectedName;
	private String paymentMethod;
	private String email;
	private Integer amount;
	private boolean rechargeWithImei;
	private String fromViewUrl;

}
