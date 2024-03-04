package com.topostechnology.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.topostechnology.constant.UserConstants;
import com.topostechnology.domain.OddoUrlOffert;
import com.topostechnology.model.PaymentModel;
import com.topostechnology.model.PlanModel;
import com.topostechnology.model.ValidUserPlans;
import com.topostechnology.repository.OddoUrlOffertRepository;
import com.topostechnology.rest.client.GategayApiXtraClient;
import com.topostechnology.rest.client.request.CitiConsultingOfferRequest;
import com.topostechnology.rest.client.response.CitiConsultingOffersResponse;
import com.topostechnology.rest.client.response.CitiOffer;
import com.topostechnology.utils.StringUtils;
import com.topostechnology.validation.GeneralValidator;

@Service
public class ExpressRechargeService {
	
	private static final String SUBSCRIBER_NOT_EXIST_MSG = "El número celular ingresado no pertenece a Turbored.";
	private static final Integer SUBSCRIBER_NOT_EXIST_MSG_CODE = 4;
	private static final Integer NOT_OFFERS_FOUND_MSG_CODE = 6;
	private static final String NOT_OFFERS_FOUND_MSG= "No se encontraron planes disponibles para el número de celular ingresado.";

	
	@Value("${mx.country.code}")
	private String mxCountryCode;
	
	@Value("${turbored.be.id}")
	private String beId;
	
	@Autowired
	private GategayApiXtraClient gategayApiXtraClient;
	
	@Autowired
	private OddoUrlOffertRepository oddoUrlOffertRepository;
	
	public String getRechargeUrl(PaymentModel rechargeModel) {
		String rechargeUrl = null;
		Long offerId =  rechargeModel.getPlanSelectedId() != null ? Long.valueOf(rechargeModel.getPlanSelectedId()) :0;
		OddoUrlOffert oddoUrlOffer = oddoUrlOffertRepository.findByofferId(offerId);
		if(oddoUrlOffer != null) {
			rechargeUrl = oddoUrlOffer.getOdooUrl();
		}
		return rechargeUrl;
	}
	
	public void cleanFormData(PaymentModel rechargeModel) {
		rechargeModel.setCellphoneNumber("");
		rechargeModel.setCellphoneNumberConfirmation("");
		rechargeModel.setPlanSelectedId(null);
	}
	
	public ValidUserPlans getUserPlans(String msisdn) throws Exception {
		ValidUserPlans validUserPlans = new ValidUserPlans();
		CitiConsultingOfferRequest citiConsultingOfferRequest = new CitiConsultingOfferRequest();
		citiConsultingOfferRequest.setBe_id(beId);
		citiConsultingOfferRequest.setMsisdn(msisdn);
		citiConsultingOfferRequest.setTransaction_id("0"); // TODO no hay, pero en el api  es obligatorio
		CitiConsultingOffersResponse citiConsultingOffersResponse = gategayApiXtraClient
				.getCitiConsultingOffer(citiConsultingOfferRequest);
		
		if (citiConsultingOffersResponse.getResponseCode().equals(SUBSCRIBER_NOT_EXIST_MSG_CODE)) {
			validUserPlans.setBelongsToTurbored(false); // No pertenece a turbored
			validUserPlans.setErrorMessage(SUBSCRIBER_NOT_EXIST_MSG);
		} else if (citiConsultingOffersResponse.getResponseCode().equals(NOT_OFFERS_FOUND_MSG_CODE)) {
			validUserPlans.setErrorMessage(NOT_OFFERS_FOUND_MSG);
		} else {
			List<CitiOffer> citiOffers = citiConsultingOffersResponse.getOffers();
			List<PlanModel> planList = new ArrayList<PlanModel>();
			for (CitiOffer offer : citiOffers) {
				PlanModel plan = new PlanModel();
				if (StringUtils.isNotBlank(offer.getOfferId()) && StringUtils.isNotBlank(offer.getSalesPrice())) {
					plan.setId(offer.getOfferId());
					Integer amount = Integer.valueOf(offer.getSalesPrice());
					plan.setAmount(amount);
					plan.setName(offer.getOfferNamePrice());
					planList.add(plan);
				}
			}
			validUserPlans.setPlanList(planList);
			validUserPlans.setCellphoneNumber(msisdn);
		}
		return validUserPlans;
	}
	
	public String validateImeiOrCellphoneNumber(String cellphoneNumberOrImei) {
		String message = null;
		if (StringUtils.isBlank(cellphoneNumberOrImei)) {
			message = "Imei o numero asociado es obligatorio";
		} else {
				boolean validCellphoneNumber = GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER,
						cellphoneNumberOrImei);
				boolean validImei = GeneralValidator.validatePattern(UserConstants.REGEX_IMEI_NUMBER, cellphoneNumberOrImei);
				if(!validCellphoneNumber && !validImei) {
					message = "El imei debe contener 15 ditos o el numero asociado 10 digitos";
				}
		}
		return message;
	}
	
}
