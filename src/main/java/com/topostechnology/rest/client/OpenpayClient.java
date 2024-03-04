package com.topostechnology.rest.client;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.topostechnology.constant.PaymentConstants;
import com.topostechnology.model.PaymentModel;

import mx.openpay.client.Charge;
import mx.openpay.client.Customer;
import mx.openpay.client.core.OpenpayAPI;
import mx.openpay.client.core.requests.transactions.CreateStoreChargeParams;

@Service
public class OpenpayClient {
	
	private static final Logger logger = LoggerFactory.getLogger(OpenpayClient.class);

	@Value("${openpay.merchant.id}")
	private String merchantId;
	
	@Value("${openpay.private.apikey}")
	private String privateApiKey;
	
	@Value("${openpay.api.url}")
	private String openpayApiUrl;
	
	@Value("${openpay.url}")
	private String openpayUrl;
	
	@SuppressWarnings("deprecation")
	public Charge createPaynetOrder(PaymentModel payment) throws Exception {
		logger.info("Creando referencia de pago para " + payment.getCellphoneNumber());
		Charge transaction = null;
		try {
		OpenpayAPI api = new OpenpayAPI(openpayApiUrl, privateApiKey, merchantId);
		String orderId = String.valueOf(System.currentTimeMillis());
		transaction = api.charges().create(
		        new CreateStoreChargeParams().amount(new BigDecimal(payment.getAmount())).description(payment.getPlanSelectedName())
		        .orderId(orderId).customer(new Customer().name(PaymentConstants.GENERAL_TURBORED_USER).email(payment.getEmail()).phoneNumber(payment.getCellphoneNumber())));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return transaction;
	}
	
	public String createPaymentUrl(Charge transaction) {
		String paymentUrl = null;
		if(transaction != null) {
			paymentUrl = openpayUrl + "paynet-pdf/" + merchantId + "/" + transaction.getPaymentMethod().getReference();
		}
		logger.info("Referencia de pago: " + paymentUrl);
		return paymentUrl;
	}

	
}
