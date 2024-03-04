package com.topostechnology.rest.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.topostechnology.exception.TrException;
import com.topostechnology.rest.client.request.PaymentLinkRequest;
import com.topostechnology.rest.client.request.PaymentLinkResponse;

@Component
public class PaymentLinkClient {
	
	@Value("${api.payment.link}")
	private String paymenlinkWs; 
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentLinkClient.class);
	
	public PaymentLinkResponse getPaymentLink(PaymentLinkRequest paymentLinkRequest) throws TrException {
		logger.info("Obteniendo link de pago");
		PaymentLinkResponse paymentLinkResponse = null;
		ResponseEntity<PaymentLinkResponse> response = null;
		HttpEntity<PaymentLinkRequest> request = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			request = new HttpEntity<>(paymentLinkRequest, headers);
			response = restTemplate.exchange(paymenlinkWs, HttpMethod.POST, request,
					PaymentLinkResponse.class);
			int status = response.getStatusCodeValue();
			if (status == 200) {
				paymentLinkResponse = response.getBody();
				logger.info("Response payment link: " + paymentLinkResponse.getUrl());
			} else {
				logger.error("Error " + status);
				throw new TrException("El link de pago no pudo ser generado, intenta nuevamente.");
			}
		} catch (Exception ex) {
			logger.error("Error no se pudo obtener el link de pago " + paymentLinkRequest.getMsisdn() + "-"  + ex.getMessage());
			throw new TrException("El link de pago no pudo ser generado, intenta nuevamente.");
		}
		return paymentLinkResponse;
	}
	

}
