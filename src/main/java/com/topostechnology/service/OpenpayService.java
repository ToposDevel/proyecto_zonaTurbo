package com.topostechnology.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.topostechnology.constant.PaymentConstants;
import com.topostechnology.domain.OpenpayOrder;
import com.topostechnology.domain.OpenpayOrderEvent;
import com.topostechnology.exception.TrException;
import com.topostechnology.model.PaymentModel;
import com.topostechnology.repository.OpenpayOrderEventRepository;
import com.topostechnology.repository.OpenpayOrderRepository;
import com.topostechnology.rest.client.OpenpayClient;
import com.topostechnology.utils.StringUtils;
import com.topostechnology.webhook.data.OpenpayEventData;

import mx.openpay.client.Charge;

@Service
public class OpenpayService  extends PaymentService {

	@Autowired
	private EmailService emailService;
	
	@Value("${callcenter.phone}")
	private String callCenterPhone;
	
	@Autowired
	private OpenpayOrderRepository openpayOrderRepository;
	
	@Autowired
	private OpenpayOrderEventRepository openpayOrderEventRepository;
	
	@Autowired
	private OpenpayClient openpayClient;
	
	@Value("${openpay.merchant.id}")
	private String merchantId;
	
	@Autowired
	private SmsService smsService;
	
	private static final Logger logger = LoggerFactory.getLogger(OpenpayService.class);
	
	
	public String CreateOrder(PaymentModel paymentModel) throws TrException {
		String paymentUrl = null;
		OpenpayOrder openpayOrder = null;
		try {
			Charge transaction = openpayClient.createPaynetOrder(paymentModel);
			if(transaction != null) {
				openpayOrder = this.saveOrder(paymentModel, transaction);
				paymentUrl = openpayClient.createPaymentUrl(transaction);
				this.sendPaymentPdfNotification(paymentModel, paymentUrl);
				logger.info("Link payment. " +  paymentUrl );
			}
		} catch (Exception e) {
			this.sendErrorNotification(openpayOrder);
			logger.error("Error: " + e.getMessage());
			throw new TrException("La orden no pudo ser creada, intenta nuevamente.");
		}
		return paymentUrl;
	}
	
	public OpenpayOrder saveOrder(PaymentModel payment, Charge transaction) {
		logger.info("Guardando orden openpay para " + payment.getCellphoneNumber());
		OpenpayOrder openpayOrder = new OpenpayOrder();
		openpayOrder.setActive(true);
		openpayOrder.setAmount(payment.getAmount());
		openpayOrder.setOrderId(transaction.getOrderId());
		openpayOrder.setTransactionId(transaction.getId());
		openpayOrder.setEmail(payment.getEmail());
		openpayOrder.setName("");
		openpayOrder.setPhone(payment.getCellphoneNumber());
		openpayOrder.setCreatedAt(new Date());
		openpayOrder.setPaymentMethod(payment.getPaymentMethod());
		openpayOrder.setAltanOfferId(payment.getPlanSelectedId());
		openpayOrder.setInternalStatus(PaymentConstants.ORDER_PENDING_PAYMENT);
		openpayOrder.setReference(transaction.getPaymentMethod().getReference());
		openpayOrderRepository.save(openpayOrder);
		return openpayOrder;
	}
	
	public void processOrderPaidEvent(OpenpayEventData openpayEventData) throws Exception {
		logger.info("Registrando Order event en bd ");
		String orderId = openpayEventData.getTransaction().getOrderId();
		OpenpayOrder openpayOrder = openpayOrderRepository.findByOrderId(orderId);
		if(openpayOrder != null ) {
			if(StringUtils.isBlank(openpayOrder.getAltanOrderId())) {
				OpenpayOrderEvent OpenpayOrderEvent = this.createOpenPayEvent(openpayEventData);
				openpayOrderEventRepository.save(OpenpayOrderEvent);
				String altanOrderId = this.doPurchase(openpayOrder.getPhone(), openpayOrder.getAltanOfferId());
				this.sendPaymentSuccesNotification(openpayOrder);
				this.updateOrder(openpayOrder, altanOrderId, OpenpayOrderEvent.getStatus());
			} else {
				logger.info("Pago procesado anteriormente");
			}
		} else {
			throw new TrException("No se encontro orden registrada " + orderId);
		}
	}
	
	private void updateOrder(OpenpayOrder openpayOrder, String altanOrderId, String orderStatus) {
		logger.info("Actualizando OpenpayOrder ");
		openpayOrder.setAltanOrderId(altanOrderId);
		openpayOrder.setInternalStatus(orderStatus);
		openpayOrder.setUpdatedAt(new Date());
		openpayOrder.setPurchaseAltanAt(new Date());
		openpayOrderRepository.save(openpayOrder);
	}
	
	private OpenpayOrderEvent createOpenPayEvent(OpenpayEventData openpayEventData) {
		OpenpayOrderEvent openpayOrderEvent = null;
		if(openpayEventData != null) {
			openpayOrderEvent = new OpenpayOrderEvent();
			openpayOrderEvent.setAmount(openpayEventData.getTransaction().getAmount());
			openpayOrderEvent.setErrorMessage(openpayEventData.getTransaction().getErrorMessage());
			openpayOrderEvent.setEventDate(openpayEventData.getEvent_date());
			openpayOrderEvent.setEventType(openpayEventData.getType());
			openpayOrderEvent.setOperationDate(openpayEventData.getTransaction().getOperationDate());
			openpayOrderEvent.setOrderId(openpayEventData.getTransaction().getOrderId());
			openpayOrderEvent.setPaymentMethod(openpayEventData.getTransaction().getMethod());
			openpayOrderEvent.setStatus(openpayEventData.getTransaction().getStatus());
			openpayOrderEvent.setTransactionId(openpayEventData.getTransaction().getId());
			openpayOrderEvent.setActive(true);
			openpayOrderEvent.setCreatedAt(new Date());
		}
		return openpayOrderEvent;
	}
	
	public void sendPaymentPdfNotification(PaymentModel paymentModel, String paymentLink) {
		if(StringUtils.isNotBlank(paymentModel.getEmail())) {
			try {
				String message = "<p>Enlace para generar formato de pago Paynet para la recarga del numero  </>" + " " + paymentModel.getCellphoneNumber() + " " 
						+ "<a href='"+paymentLink + "'<b>Generar PDF </b></a>"
						+ "<p>*Si ya realizaste el pago, favor de hacer caso omiso a este mensaje.</p>";
				emailService.sendSimpleAutamaticFormatNotification("", paymentModel.getCellphoneNumber(), paymentModel.getEmail(), "Pago Paynet", message, null);
				String smsMessage = "Descarga el formato para tu pago Paynet " + paymentLink;
				smsService.sendSmsNotification(paymentModel.getCellphoneNumber(), smsMessage);
			} catch (Exception e) {
				logger.error("La referencia oxxo pay del número celular " +paymentModel.getCellphoneNumber() +" no pudo ser enviada " + " Error msg " + e.getMessage());
			}
		}
	}

	public void sendPaymentSuccesNotification(OpenpayOrder openpayOrder) {
		logger.info("Enviando notificación de pago exitoso. ");
		if(StringUtils.isNotBlank(openpayOrder.getEmail())) {
			try {
				String message = "Tu pago de  $ " + openpayOrder.getAmount()+ " para el número asociado " +openpayOrder.getPhone() + " ha sido procesado exitosamente."; 
				emailService.sendSimpleAutamaticFormatNotification("", openpayOrder.getPhone(), openpayOrder.getEmail(), "Pago exitoso", message, null);
				smsService.sendSmsNotification(openpayOrder.getPhone(), message);
			} catch (Exception e) {
				logger.info("La notificación de pago exitoso de " +openpayOrder.getPhone() +" no pudo ser enviada " + e.getMessage());
			}
		}
	}
	
	public void sendErrorNotification(OpenpayOrder openpayOrder) {
		logger.info("Enviando notificación de error al hacer la recarga. " + openpayOrder.getPhone());
		if(openpayOrder != null && StringUtils.isNotBlank(openpayOrder.getEmail())) {
			try {
				String message = "La recarga de  $ " + openpayOrder.getAmount()+ " para el número asociado " +openpayOrder.getPhone() + " no fue completada exitosamente, comunicate a nuestro call center " + callCenterPhone + "."; 
				emailService.sendSimpleAutamaticFormatNotification("", openpayOrder.getPhone(), openpayOrder.getEmail(), "Recarga no completada", message, null);
				smsService.sendSmsNotification(openpayOrder.getPhone(), message);
			} catch (Exception e) {
				logger.info("La notificación de error al hacer la recarga de " +openpayOrder.getPhone() +" no pudo ser enviada " + e.getMessage());
			}
		}
	}

}
