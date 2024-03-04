package com.topostechnology.service;


import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.topostechnology.constant.PaymentConstants;
import com.topostechnology.exception.TrException;
import com.topostechnology.model.PaymentModel;
import com.topostechnology.rest.client.ConektaClient;
import com.topostechnology.utils.StringUtils;

import io.conekta.Charge;
import io.conekta.Error;
import io.conekta.ErrorList;
import io.conekta.Order;
import io.conekta.OxxoPayment;

@Service
public class ConektaOxxoPayService {

	private static final Logger logger = LoggerFactory.getLogger(ConektaOxxoPayService.class);
	
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

	public String generateReference(PaymentModel conectaOxxoPaymentModel) throws TrException {
		String oxxoReference = null;
		Order order = this.createOrder(conectaOxxoPaymentModel);
		Charge charge = (Charge) order.charges.get(0); // TODO VALIDAR
		OxxoPayment oxxoPayment = (OxxoPayment) charge.payment_method;
		oxxoReference = oxxoPayment.reference;
		logger.info("Referencia oxxo para el número celular " + conectaOxxoPaymentModel.getCellphoneNumber());
		if(StringUtils.isNotBlank(oxxoReference)) {
			this.sendNotification(conectaOxxoPaymentModel, oxxoReference);
		}
		try{
			conektaPaymentService.saveOrderInDb(order, PaymentConstants.OXXO_PAYMENT_METHOD, conectaOxxoPaymentModel.getPlanSelectedId());
		} catch(Exception e) {
			throw new TrException("Se ha generado un error al generar la referencia oxxo pay para el número celular "
					+ conectaOxxoPaymentModel.getCellphoneNumber() + "intenta nuevamente.");
		}
		return oxxoReference;
	}
	
	public Order createOrder(PaymentModel conectaOxxoPaymentModel) throws TrException {
		Order order;
		try {
			order = conektaClient.createOxxoOrder(conectaOxxoPaymentModel.getPlanSelectedName(),
					conectaOxxoPaymentModel.getAmount(), conectaOxxoPaymentModel.getEmail(),
					conectaOxxoPaymentModel.getCellphoneNumber());
		
		} catch (ErrorList e) {
			String errorStr= "";
			ArrayList<Error> details = e.details;
			for (io.conekta.Error error : details) {
				errorStr =   errorStr + " " + error.getMessage();
			}
			logger.error(errorStr);
			throw new TrException("La referencia no pudo ser generada, verifique sus datos e intente nuevamente");
		} catch (Exception e) {
			logger.error("Se ha generado un error al generar la referencia oxxo para el número celular "
					+ conectaOxxoPaymentModel.getCellphoneNumber());
			logger.error(e.getLocalizedMessage());
			throw new TrException("Se ha generado un error al generar la referencia de oxxo para el número celular "
					+ conectaOxxoPaymentModel.getCellphoneNumber() + "intenta nuevamente.");
		}
		return order;
	}
	
	public void sendNotification(PaymentModel conectaOxxoPaymentModel, String oxxoReference) {
		if(StringUtils.isNotBlank(conectaOxxoPaymentModel.getEmail())) {
			try {
				String basicMessage = "El número de referencia para poder realizar el pago para la recarga del numero " +conectaOxxoPaymentModel.getCellphoneNumber() + " en OXXO con Oxxo pay  es " + oxxoReference ; 
				String message = basicMessage + ", no es necesario imprimir."; 
				emailService.sendSimpleAutamaticFormatNotification("", conectaOxxoPaymentModel.getCellphoneNumber(), conectaOxxoPaymentModel.getEmail(), "Referencia Oxxo pay", message, null);
				smsService.sendSmsNotification(conectaOxxoPaymentModel.getCellphoneNumber(), basicMessage);
			} catch (Exception e) {
				logger.error("La referencia oxxo pay del número celular " +conectaOxxoPaymentModel.getCellphoneNumber() +" no pudo ser enviada  a " + conectaOxxoPaymentModel.getCellphoneNumber() + " Error msg " + e.getMessage());
			}
		}
	}
	
}
