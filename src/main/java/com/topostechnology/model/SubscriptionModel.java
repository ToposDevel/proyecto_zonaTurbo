package com.topostechnology.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionModel {
	
	private String tokenId;
	private String email;
	private String cellphoneNumber;
	private String cellphoneNumberConfirmation;
	private List<PlanModel> plans;
	private String planSelectedId;
	private String cardTitularName;
	
    private boolean suscriptionWithImei;
    private String imei;
    private String imeiConfirmation;
    private boolean belongsToHhb;
    private String coordinates;
    private String internalOperation;
    private String superOfferType;
    
    private String fromViewUrl;
    

}
