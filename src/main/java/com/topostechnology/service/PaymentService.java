package com.topostechnology.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.topostechnology.constant.UserConstants;
import com.topostechnology.model.PaymentModel;
import com.topostechnology.rest.client.GategayApiClient;
import com.topostechnology.rest.client.request.PurchaseRequest;
import com.topostechnology.rest.client.response.AltanActionResponse;
import com.topostechnology.rest.client.response.PurchaseResponse;
import com.topostechnology.utils.StringUtils;
import com.topostechnology.validation.GeneralValidator;

@Service
public class PaymentService {
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
	
	@Autowired
	private GategayApiClient gategayApiClient;
	
	@Value("${turbored.be.id}")
	private String beId;
	
	@Autowired
	private AltanActionRequestService altanActionRequestService;
	
	
	
	public BindingResult validateFormDataForPaymentMethodRecharge(PaymentModel rechargeModel, BindingResult bindingResult) {


		   if (StringUtils.isBlank(rechargeModel.getPaymentMethod())) {
		      bindingResult.rejectValue("paymentMethod", "mandatory.field");
		   }

		   return bindingResult;
		}
	
	public BindingResult validateFormDataForPayment(PaymentModel rechargeModel, BindingResult bindingResult) {

		if (StringUtils.isBlank(rechargeModel.getCellphoneNumber())) {
			bindingResult.rejectValue("cellphoneNumber", "mandatory.field");
		} else if (!GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER,
				rechargeModel.getCellphoneNumber())) {
			bindingResult.rejectValue("cellphoneNumber", "cellphone.number.then");
		}
		if (StringUtils.isBlank(rechargeModel.getPlanSelectedId())) {
			bindingResult.rejectValue("planSelectedId", "mandatory.field");
		}

		if (StringUtils.isBlank(rechargeModel.getPlanSelectedName())) {
			bindingResult.rejectValue("planSelectedName", "mandatory.field");
		}

		if (StringUtils.isBlank(rechargeModel.getPaymentMethod())) {
			bindingResult.rejectValue("paymentMethod", "mandatory.field");
		}

		return bindingResult;
	}

	public BindingResult validateBasicFormData(PaymentModel rechargeModel, BindingResult bindingResult) {
		if (!GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER,
				rechargeModel.getCellphoneNumber())) {
			bindingResult.rejectValue("cellphoneNumber", "cellphone.number.then");
		}
		if (!GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER,
				rechargeModel.getCellphoneNumberConfirmation())) {
			bindingResult.rejectValue("cellphoneNumberConfirmation", "cellphone.number.then");
		}
		if (!rechargeModel.getCellphoneNumber().equals(rechargeModel.getCellphoneNumberConfirmation())) {
			bindingResult.rejectValue("cellphoneNumber", "cellphone.not.equal.message");
		}
		if (StringUtils.isBlank(rechargeModel.getPlanSelectedId())) {
			bindingResult.rejectValue("planSelectedId", "mandatory.field");
		}

		if (StringUtils.isBlank(rechargeModel.getPlanSelectedName())) {
			bindingResult.rejectValue("planSelectedName", "mandatory.field");
		}

		return bindingResult;
	}
	
	public String doPurchase(String associatedNumber, String offerId) throws Exception {
		logger.info("Haciendo recarga de oferta " + offerId + " en altan para el número asociado" + associatedNumber);
		String altanOrderId = null;
		if (StringUtils.isNotBlank(offerId) && StringUtils.isNotBlank(associatedNumber)) {
			PurchaseRequest purchaseRequest = new PurchaseRequest();
			List<String> offerings = new ArrayList<String>();
			offerings.add(offerId);
			purchaseRequest.setOfferings(offerings);
			purchaseRequest.setBeId(beId);
			purchaseRequest.setMsisdn(associatedNumber);
			PurchaseResponse purchaseResponse  = gategayApiClient.purchase(purchaseRequest);
			AltanActionResponse altanActionResponse = purchaseResponse.getAltanActionResponse();
			altanOrderId = altanActionResponse.getAltanOrderId();
			altanActionRequestService.save(associatedNumber, altanActionResponse);
		} else {
			logger.error(
					"No se pudo realizar la recarga  de la  oferta,  offerId y cellphoneNumber son necesarios " + associatedNumber);
		}
		return altanOrderId;
	}

	public BindingResult validateFormDataForPaymentOxxoPaynet(PaymentModel rechargeModel, BindingResult bindingResult) {


		   if (!GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER,
		         rechargeModel.getCellphoneNumber())) {
		      bindingResult.rejectValue("cellphoneNumber", "cellphone.number.then");
		   }
		   if (!GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER,
		         rechargeModel.getCellphoneNumberConfirmation())) {
		      bindingResult.rejectValue("cellphoneNumberConfirmation", "cellphone.number.then");
		   }
		   if (!rechargeModel.getCellphoneNumber().equals(rechargeModel.getCellphoneNumberConfirmation())) {
		      bindingResult.rejectValue("cellphoneNumber", "cellphone.not.equal.message");
		   }
		   if (StringUtils.isBlank(rechargeModel.getEmail())) {
		      bindingResult.rejectValue("email", "mandatory.field");
		   }
		   if (StringUtils.isBlank(rechargeModel.getPlanSelectedId())) {
		      bindingResult.rejectValue("planSelectedId", "mandatory.field");
		   }

		   if (StringUtils.isBlank(rechargeModel.getPlanSelectedName())) {
		      bindingResult.rejectValue("planSelectedName", "mandatory.field");
		   }

		   if (StringUtils.isBlank(rechargeModel.getPaymentMethod())) {
		      bindingResult.rejectValue("paymentMethod", "mandatory.field");
		   }



		   return bindingResult;
		}



}
