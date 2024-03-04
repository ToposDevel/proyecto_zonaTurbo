package com.topostechnology.service;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.topostechnology.constant.PaymentConstants;
import com.topostechnology.constant.UserConstants;
import com.topostechnology.exception.TrException;
import com.topostechnology.model.CardPaymentModel;
import com.topostechnology.model.PaymentModel;
import com.topostechnology.rest.client.ConektaClient;
import com.topostechnology.utils.StringUtils;
import com.topostechnology.validation.GeneralValidator;

import io.conekta.Error;
import io.conekta.ErrorList;
import io.conekta.Order;

@Service
public class ConektaCardService {

	private static final Logger logger = LoggerFactory.getLogger(ConektaCardService.class);

	@Autowired
	private ConektaClient conektaClient;

	@Autowired
	private EmailService emailService;

	@Autowired
	private ConektaPaymentService conektaPaymentService;

	@Value("${callcenter.phone}")
	private String callCenterPhone;
	
	@Autowired
	private SmsService smsService;

	public CardPaymentModel createCardPaymentModel(PaymentModel paymentModel) throws TrException {
		CardPaymentModel cardPaymentModel = new CardPaymentModel();
		cardPaymentModel.setAmount(paymentModel.getAmount());
		cardPaymentModel.setCellphoneNumber(paymentModel.getCellphoneNumber());
		cardPaymentModel.setPaymentMethod(paymentModel.getPaymentMethod());
		cardPaymentModel.setPlanSelectedId(paymentModel.getPlanSelectedId());
		cardPaymentModel.setPlanSelectedName(paymentModel.getPlanSelectedName());
		cardPaymentModel.setEmail(paymentModel.getEmail());
		return cardPaymentModel;
	}
	
	public Order createOrder(CardPaymentModel cardPaymentModel) throws TrException, ErrorList, Error, JSONException {
		logger.info("Creando orden Conekta para pago con tarjeta");
		Order order = conektaClient.createCardOrder(cardPaymentModel.getPlanSelectedName(),
				cardPaymentModel.getAmount(), cardPaymentModel.getEmail(), cardPaymentModel.getCellphoneNumber(),
				cardPaymentModel.getTokenId(), cardPaymentModel.getCardTitularName());
		try {
			conektaPaymentService.saveOrderInDb(order, PaymentConstants.CARD_CONEKTA_PAYMENT_METHOD,
					cardPaymentModel.getPlanSelectedId());
			sendProcessingPaymentNotification(cardPaymentModel);
		} catch (Exception e) {
			logger.info("La orden conekta no pudo registrase en bd  " + e.getMessage());
		}

		return order; 
	}

	public void sendProcessingPaymentNotification(PaymentModel conectaOxxoPaymentModel) {
		if (StringUtils.isNotBlank(conectaOxxoPaymentModel.getEmail())) {
			try {
				String message = "El pago de la recarga del número celular  " + conectaOxxoPaymentModel.getCellphoneNumber()
						+ " está en proceso.";
				emailService.sendSimpleAutamaticFormatNotification("", conectaOxxoPaymentModel.getCellphoneNumber(),
						conectaOxxoPaymentModel.getEmail(), "Cargo de recarga en proceso", message, null);
				smsService.sendSmsNotification(conectaOxxoPaymentModel.getCellphoneNumber(), message);
			} catch (Exception e) {
				logger.error("La notificacion por pago con tarjeta de recarga en proceso  no pudo ser enviada  a " + conectaOxxoPaymentModel.getCellphoneNumber() 
				+" Error msg " + e.getMessage());
			}
		}
	}

	public BindingResult validateCardPaymentData(CardPaymentModel cardPaymentModel, BindingResult bindingResult) {
		if (StringUtils.isBlank(cardPaymentModel.getCardTitularName())) {
			bindingResult.rejectValue("customerName", "mandatory.field");
		}
		if (StringUtils.isBlank(cardPaymentModel.getPlanSelectedId())) {
			bindingResult.rejectValue("planSelectedId", "mandatory.field");
		}
		if (cardPaymentModel.getAmount() == null || cardPaymentModel.getAmount() <= 0) {
			bindingResult.rejectValue("amount", "mandatory.field");
		}
		if (!GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER,
				cardPaymentModel.getCellphoneNumber())) {
			bindingResult.rejectValue("cellphoneNumber", "cellphone.number.then");
		}
		if (StringUtils.isBlank(cardPaymentModel.getTokenId())) {
			bindingResult.rejectValue("tokenId", "mandatory.field");
		}
		return bindingResult;
	}

}
