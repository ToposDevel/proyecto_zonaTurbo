package com.topostechnology.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.topostechnology.constant.ResponseCodeConstants;
import com.topostechnology.exception.TrException;
import com.topostechnology.rest.client.GategayApiClient;
import com.topostechnology.rest.client.request.SendSmsRequest;
import com.topostechnology.rest.client.response.ApiGatewayGeneralResponse;

@Service
public class SmsService {
	
	private static final Logger logger = LoggerFactory.getLogger(SmsService.class);
	
	@Value("${mx.country.code}")
	private String mxCountryCode;
	
	@Value("${apigateway.sendSms.user}")
	private String sendSmsUser;

	@Value("${apigateway.sendSms.password}")
	private String sendSmsPassword;
	
	@Autowired
	private GategayApiClient gategayApiClient;
	
	public void sendSmsNotification(String phoneNumber, String message) throws TrException {
		try {
			SendSmsRequest sendSmsRequest = new SendSmsRequest();
			sendSmsRequest.setMessage(message);
			sendSmsRequest.setNumber(mxCountryCode + phoneNumber);
			sendSmsRequest.setPassword(sendSmsPassword);
			sendSmsRequest.setUsuario(sendSmsUser);
			ApiGatewayGeneralResponse apiGatewayGeneralResponse = gategayApiClient.sendSms(sendSmsRequest);
			if(!apiGatewayGeneralResponse.getCodigo().equals(ResponseCodeConstants.SUCCES_CODE_STR)) {
				logger.error("Error al intentar envias SMS al numero {} Error {}", phoneNumber,  apiGatewayGeneralResponse.getMensaje());
				throw new TrException("The code could not be sent.");
			}
		} catch (Exception e) {
			logger.error("La notificación SMS a " + phoneNumber + " no pudo ser enviada Error msg " + e.getMessage());
			throw e;
		}
	}

}
