package com.topostechnology.service;

import java.util.Date;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.topostechnology.constant.ConektaEventConstants;
import com.topostechnology.domain.BinacleSubscription;
import com.topostechnology.domain.ConektaOrderEvent;
import com.topostechnology.domain.ConektaSubscriptionEvent;
import com.topostechnology.exception.TrException;
import com.topostechnology.repository.BinacleSubscriptionRepository;
import com.topostechnology.webhook.data.OrderEvent;
import com.topostechnology.webhook.data.SubscriptionEvent;
import com.topostechnology.webhook.data.WebhookResponse;

@Service
public class ConektaWehookService {
	
	@Autowired
	private ConektaSubscriptionService conektaSubscriptionService;
	
	@Autowired
	private ConektaPaymentService conektaPaymentService;
	
	@Autowired
	private BinacleSubscriptionRepository  binacleSubscriptionRepository;

	private static final Logger logger = LoggerFactory.getLogger(ConektaWehookService.class);

	public void proccessEvents(WebhookResponse webhookResponse, String jsonResponse) {
		logger.info("Recibiendo notificación de evento conekta ");
		if(webhookResponse != null) {
			ConektaSubscriptionEvent conektaSubscriptionEvent ;
			ConektaOrderEvent conektaOrderEvent ;
			String event = webhookResponse.getType();
			logger.info("Evento conekta: "  + event);
			switch (event) {
			case ConektaEventConstants.SUBSCRIPTION_CREATED:
				logger.info("Evento de Suscripción creada -> " + event + " " + webhookResponse.getId());
				this.saveSubscriptionEvent(webhookResponse, jsonResponse);
				// SI NO EXISTE REGISTRO DE SUSCRIPCION, ENTONCES CREARLO
				break;
			case ConektaEventConstants.SUBSCRIPTION_PAUSED:
				logger.info("Evento  de Suscripción pausada -> " + event + " " + webhookResponse.getId());
				this.saveSubscriptionEvent(webhookResponse, jsonResponse);
				break;
			case ConektaEventConstants.SUBSCRIPTION_RESUMED:
				logger.info("Evento de Suscripción resumida ->" + event + " " + webhookResponse.getId());
				this.saveSubscriptionEvent(webhookResponse, jsonResponse);
				break;
			case ConektaEventConstants.SUBSCRIPTION_CANCELED:
				logger.info("Evento de Suscripción cancelada ->" + event + " " + webhookResponse.getId());
				// VALIDAR SI LA SUSCRIPCION HA SOLICITADO SER CANCELADA 
				conektaSubscriptionEvent = this.saveSubscriptionEvent(webhookResponse, jsonResponse);
				conektaSubscriptionService.processSubscriptionCanceledEvent(conektaSubscriptionEvent);
				break;
			case ConektaEventConstants.SUBSCRIPTION_EXPIRED:
				logger.info("Evento  de Suscripción expirada ->" + event + " " + webhookResponse.getId());
				this.saveSubscriptionEvent(webhookResponse, jsonResponse);
				// TODO
				break;
			case ConektaEventConstants.SUBSCRIPTION_UPDATED:
				logger.info("Evento  de Suscripción actualizada ->" + event + " " + webhookResponse.getId());
				this.saveSubscriptionEvent(webhookResponse, jsonResponse);
				break;
			case ConektaEventConstants.SUBSCRIPTION_PAID:
				logger.info("Evento  de Suscripción pagada ->" + event + " " + webhookResponse.getId());
				conektaSubscriptionEvent = this.saveSubscriptionEvent(webhookResponse, jsonResponse);
				conektaSubscriptionService.processSubscriptionPaidEvent(conektaSubscriptionEvent); // TOTO REGRESAR
				break;
			case ConektaEventConstants.SUBSCRIPTION_PAYMENT_FAILED:
				logger.info("Evento  de Fallo en pago de suscripción ->" + event + " " + webhookResponse.getId());
				conektaSubscriptionEvent = this.saveSubscriptionEvent(webhookResponse, jsonResponse);
				conektaSubscriptionService.processSubscriptionPaymentFailedEvent(conektaSubscriptionEvent);
				break;
			case ConektaEventConstants.ORDER_PAID:
				logger.info("Evento  de orden pagada ->" + event + " " + webhookResponse.getId());
				conektaOrderEvent = this.saveOrderEvent(webhookResponse, jsonResponse);
				conektaPaymentService.processOrderPaidEvent(conektaOrderEvent);
				break;
			default:
				// TODO REGISTRAR EVENTOS
				logger.info("Este evento no pertenenece a  la suscripción ni pago de orden->" + event);
			}
		}
	}
	
	private ConektaSubscriptionEvent saveSubscriptionEvent(WebhookResponse webhookResponse, String jsonResponse) {
		SubscriptionEvent subscriptionEvent = this.createSubscriptionEventObject(webhookResponse, jsonResponse);
		ConektaSubscriptionEvent conektaSubscriptionDetail = conektaSubscriptionService.saveSubscriptionEventInDb(subscriptionEvent, webhookResponse.getType());
		
		if(conektaSubscriptionDetail!=null) {
		BinacleSubscription binacleSubscription = new BinacleSubscription(); 
		binacleSubscription.setCreateAt(new Date());
		binacleSubscription.setInternalStatus("Webhook: "+webhookResponse.getType());
		binacleSubscription.setPhone(conektaSubscriptionDetail.getConektaSubscription().getCellphoneNumber());
		binacleSubscriptionRepository.save(binacleSubscription);
		}
		return conektaSubscriptionDetail;
	}
	
	private ConektaOrderEvent saveOrderEvent(WebhookResponse webhookResponse, String jsonResponse) {
		OrderEvent orderEvent= this.createOrderEventObject(webhookResponse, jsonResponse);
		ConektaOrderEvent conektaOrderEvent = null;
		try {
			conektaOrderEvent = conektaPaymentService.saveOrderEventInDb(orderEvent, webhookResponse.getType());
		} catch (TrException e) {
			logger.error("No se pudo procesar el evento de la orden " + e.getMessage());
		}
		return conektaOrderEvent;
	}
	
	private SubscriptionEvent createSubscriptionEventObject(WebhookResponse webhookResponse, String jsonResponse) {
		Gson gson = new Gson();
		SubscriptionEvent subscriptionEvent = null;
		if(webhookResponse.getData() != null && webhookResponse.getData().getObject() != null) {
		      JSONObject jsonObject = new JSONObject(jsonResponse);
				JSONObject responseSubscriberJson = jsonObject.getJSONObject("data");
				jsonObject = new JSONObject(responseSubscriberJson.toString());
				JSONObject objectJson = jsonObject.getJSONObject("object");
				subscriptionEvent = gson.fromJson(objectJson.toString(), SubscriptionEvent.class);
		} else {
			logger.error("No viene la informacion del objeto del evento");
			// TODO No viene la informacion del objeto
		}
		return subscriptionEvent;
	}
	
	private OrderEvent createOrderEventObject(WebhookResponse webhookResponse, String jsonResponse) {
		Gson gson = new Gson();
		OrderEvent event = null;
		if(webhookResponse.getData() != null && webhookResponse.getData().getObject() != null) {
		      JSONObject jsonObject = new JSONObject(jsonResponse);
				JSONObject responseSubscriberJson = jsonObject.getJSONObject("data");
				jsonObject = new JSONObject(responseSubscriberJson.toString());
				JSONObject objectJson = jsonObject.getJSONObject("object");
				event = gson.fromJson(objectJson.toString(), OrderEvent.class);
		} else {
			logger.error("No viene la informacion del objeto del evento");
			// TODO No viene la informacion del objeto
		}
		return event;
	}
	
	
}
