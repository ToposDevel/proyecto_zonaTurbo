package com.topostechnology.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardPaymentModel extends PaymentModel {
	private String tokenId;
	private String cardTitularName;
}
