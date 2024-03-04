package com.topostechnology.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.topostechnology.constant.OpenpayConstants;
import com.topostechnology.webhook.data.OpenpayEventData;

@Service
public class OpenpayWehookService {
	
	@Autowired
	private OpenpayService openpayService;
	
	
	private static final Logger logger = LoggerFactory.getLogger(OpenpayWehookService.class);

	public void proccessEvents(OpenpayEventData openPayEventData) throws Exception {
		logger.info("Recibiendo notificación de evento openpay ");
		
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String jsonRequest = ow.writeValueAsString(openPayEventData);
		logger.info("openPayEventData: " + jsonRequest);
		
		if(openPayEventData != null) {
			String event = openPayEventData.getType();
			logger.info("Evento openpay: "  + event);
			switch (event) {
			case OpenpayConstants.CHARGE_SUCCEDED:
				logger.info("Evento de de cargo exitoso -> " + event + " " + openPayEventData.getTransaction().getOrderId() );
				this.processOrderPaidEvent(openPayEventData);
				break;
			default:
				logger.info("Evento ->" + event);
			}
		}
	}
	
	
	private void processOrderPaidEvent(OpenpayEventData openpayEventData) {
		try {
			openpayService.processOrderPaidEvent(openpayEventData);
		} catch (Exception e) {
			logger.error("No se pudo procesar el evento de la orden " + openpayEventData.getTransaction().getOrderId() + " " + e.getMessage());
		}
	}
	
}
