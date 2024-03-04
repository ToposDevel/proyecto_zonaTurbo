package com.topostechnology.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.topostechnology.constant.ConektaEventConstants;
import com.topostechnology.constant.PaymentConstants;
import com.topostechnology.domain.ConektaOrder;
import com.topostechnology.domain.ConektaOrderEvent;
import com.topostechnology.exception.TrException;
import com.topostechnology.model.OdooInvoicing;
import com.topostechnology.model.PlanModel;
import com.topostechnology.repository.ConektaOrderEventRepository;
import com.topostechnology.repository.ConektaOrderRepository;
import com.topostechnology.rest.client.ConektaClient;
import com.topostechnology.utils.DateUtils;
import com.topostechnology.utils.StringUtils;
import com.topostechnology.webhook.data.OrderEvent;

import io.conekta.Error;
import io.conekta.ErrorList;
import io.conekta.Order;
import io.conekta.Plan;

@Service
public class ConektaPaymentService  extends PaymentService {

	@Autowired
	private ConektaClient conektaClient;
	
	@Autowired
	private ConektaOrderRepository conektaOrderRepository;
	
	@Autowired
	private ConektaOrderEventRepository conektaOrderEventRepository;
	
	@Autowired
	private InvoicingService invoicingService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private SmsService smsService;

	@Value("${turbored.be.id}")
	private String beId;

	@Value("${iva.percentage}")
	private Integer ivaPercentage;
	
	@Value("${callcenter.phone}")
	private String callCenterPhone;
	
	private static final Logger logger = LoggerFactory.getLogger(ConektaPaymentService.class);
	
	public static final String EXPRESS_PLUS_RECHARGE="RecargasExpresPlus";
	public static final String EXPRESS_PLUS_RECHARGE_UPER="RECARGA EXPRES PLUS";

	public PlanModel getPlanfromconekta(String offerId) throws Error, ErrorList {
		PlanModel planModel = null;
		Plan plan = conektaClient.getPlanById(offerId);
		if(plan != null) {
			planModel = new PlanModel();
			planModel.setId(plan.id);
			planModel.setName(plan.name);
			planModel.setAmount(plan.amount);
		}
		return planModel;
	}
	
	public void saveOrderInDb(Order order, String paymentMethod, String altanOfferId) {
		logger.info("");
		ConektaOrder conektaOrder = new ConektaOrder();
		conektaOrder.setActive(true);
		conektaOrder.setAmount(order.amount/100);
		conektaOrder.setConektaCreatedAt(order.created_at);
		conektaOrder.setConektaCustomerId(order.customer_info.customer_id);
		conektaOrder.setConektaOrderId(order.id);
		conektaOrder.setEmail(order.customer_info.email);
		conektaOrder.setName(order.customer_info.name);
		conektaOrder.setPhone(order.customer_info.phone);
		conektaOrder.setCreatedAt(new Date());
		conektaOrder.setPaymentMethod(paymentMethod);
		conektaOrder.setConektaPlanId(null);
		conektaOrder.setAltanOfferId(altanOfferId);
		conektaOrder.setInternalStatus(PaymentConstants.ORDER_PENDING_PAYMENT);
		conektaOrderRepository.save(conektaOrder);
	}
	
	public void updateOrder(ConektaOrder conektaOrder, String altanOrderId, String orderStatus) {
		conektaOrder.setAltanOrderId(altanOrderId);
		conektaOrder.setInternalStatus(orderStatus);
		conektaOrder.setUpdatedAt(new Date());
		conektaOrder.setPurchaseAltanAt(new Date());
		conektaOrderRepository.save(conektaOrder);
	}

	public ConektaOrderEvent saveOrderEventInDb(OrderEvent orderEvent, String eventType) throws TrException {
		logger.info("Registrando Order event en bd ");
		ConektaOrderEvent conektaOrderEvent = null;
		ConektaOrder conektaOrder = conektaOrderRepository.findByConektaOrderId(orderEvent.getId());
		if (conektaOrder != null) {
			conektaOrderEvent = saveConektaOrderEvent(orderEvent, conektaOrder, eventType);
			logger.info("OrderEvent registrado en bd " + conektaOrderEvent.getId());
		} else {
			logger.error("No existe Orden registrada en bd para el evento  " + eventType + " " + orderEvent.getId());
		}
		return conektaOrderEvent;
	}
	
	/**
	 * Inserta registro en bd de evento generado(tabla conekta_order_event)
	 * 
	 * @param orderEvent
	 * @param conektaEvent
	 * @param event
	 * @return
	 * @throws TrException 
	 */
	private ConektaOrderEvent saveConektaOrderEvent(OrderEvent orderEvent,
			ConektaOrder conektaOrder, String eventType) throws TrException {
		ConektaOrderEvent conektaOrderEvent = null;
		if (conektaOrder != null) { // SOLO SE REGISTRAN MOVIMIENTOS DE LOS QUE EXISTA REGISTRO EN BD(hechos desde zonaturbo)
			conektaOrderEvent = createConektaOrderEventObject(orderEvent, conektaOrder,
					eventType);
			conektaOrderEventRepository.save(conektaOrderEvent);
		} else {
			logger.error("No existe Orden registrada en bd para el evento  "
					+ orderEvent.getId());
			// TODO Consultar Order en conekta y registrar Order y detalle en
			// bd VERIFICAR ordenes oddo(erp) y de  zonaturbo
		}
		if (conektaOrderEvent != null && conektaOrderEvent.getId() != null) {
			logger.info("El evento Order se registro en bd exitosamente");
		} else {
			logger.error("El evento Order NO se registro en bd.");
		}
		return conektaOrderEvent;
	}
	
	
	/**
	 * Crear objeto ConektaOrderEvent para persistir en bd
	 * 
	 * @param orderEvent
	 * @param conektaOrder
	 * @param event
	 * @return conektaOrderEvent
	 * @throws TrException 
	 */
	private ConektaOrderEvent createConektaOrderEventObject(OrderEvent orderEvent, ConektaOrder conektaOrder,
			String eventType) throws TrException {
		ConektaOrderEvent conektaOrderEvent = new ConektaOrderEvent();
		conektaOrderEvent.setActive(true);
		conektaOrderEvent.setAmount(Integer.valueOf(orderEvent.getAmount()));
		conektaOrderEvent.setAmountRefunded(conektaOrderEvent.getAmountRefunded());
		conektaOrderEvent.setConektaCreatedAt(Integer.valueOf(orderEvent.getCreated_at()));
		conektaOrderEvent.setConektaOrder(conektaOrder);
		conektaOrderEvent.setCreatedAt(new Date());
		conektaOrderEvent.setCurrency(orderEvent.getCurrency());
		conektaOrderEvent.setEventType(eventType);
		conektaOrderEvent.setPaymentMethod(conektaOrder.getPaymentMethod());
		conektaOrderEvent.setPaymentStatus(orderEvent.getPayment_status());
		return conektaOrderEvent;
	}
	
	public void processOrderPaidEvent(ConektaOrderEvent conektaOrderEvent) {
		logger.info("Procesando evento de pago de orden");
		if (conektaOrderEvent != null) {
			ConektaOrder conektaOrder = conektaOrderEvent.getConektaOrder();
			if(StringUtils.isBlank(conektaOrder.getAltanOrderId())){
				try {
					if(conektaOrderEvent.getPaymentStatus().equals(PaymentConstants.ORDER_PAID)) {
						String altanOrderId = doPurchase(conektaOrder.getPhone(), conektaOrder.getAltanOfferId());
						if(StringUtils.isNotBlank(altanOrderId)) {
							updateOrder(conektaOrderEvent.getConektaOrder(), altanOrderId, PaymentConstants.ORDER_PAID);
							sendPaymentSuccesNotification(conektaOrder);
						} else {
							logger.info("La recarga no pudo realizarse");
							this.sendErrorNotification(conektaOrder);
						}
					}
				} catch (Exception e) {
					logger.info("No se completo el proceso  ORDER PAID " + e.getMessage());
				}
			} else {
				logger.info("Pago procesado anteriormente ");
			}
			
		}
	}
	
	public void sendPaymentSuccesNotification(ConektaOrder conektaOrder) {
		logger.info("Enviando notificación de pago exitoso. ");
		if(StringUtils.isNotBlank(conektaOrder.getEmail())) {
			try {
				String message = "Tu pago de  $ " + conektaOrder.getAmount()+ " para el número asociado " +conektaOrder.getPhone() + " ha sido procesado exitosamente."; 
				emailService.sendSimpleAutamaticFormatNotification("", conektaOrder.getPhone(), conektaOrder.getEmail(), "Pago exitoso", message, null);
				smsService.sendSmsNotification(conektaOrder.getPhone(), message);
			} catch (Exception e) {
				logger.info("La notificación de pago exitoso de " +conektaOrder.getPhone() +" no pudo ser enviada por correo a " + conektaOrder.getEmail());
			}
		}
	}
	
	public void sendErrorNotification(ConektaOrder conektaOrder) {
		logger.info("Enviando notificación de error al hacer la recarga. " + conektaOrder.getPhone());
		if(StringUtils.isNotBlank(conektaOrder.getEmail())) {
			try {
				String message = "La recarga de  $ " + conektaOrder.getAmount()+ " para el número asociado " +conektaOrder.getPhone() + " no fue completada exitosamente, comunicate a nuestro call center " + callCenterPhone + "."; 
				emailService.sendSimpleAutamaticFormatNotification("", conektaOrder.getPhone(), conektaOrder.getEmail(), "Recarga no completada", message, null);
				smsService.sendSmsNotification(conektaOrder.getPhone(), message);
			} catch (Exception e) {
				logger.info("La notificación de error al hacer la recarga de " +conektaOrder.getPhone() +" no pudo ser enviada por correo a " + conektaOrder.getEmail());
			}
		}
	}
	
	/**
	 * Consulta los eventos de pagos de recarga expres para el día anterior de la fecha
	 * recibida
	 * 
	 * @param todayDate
	 * @return conektaOrderEventList lista de eventos tipo pago
	 */
	public List<ConektaOrderEvent> getOrderPaidEvents(Date todayDate) {
		List<OdooInvoicing> odooInvoicingList = new ArrayList<OdooInvoicing>();
		Date yesterdayDate = DateUtils.addOrRemoveDays(todayDate, -1);
		Date yesterdayInitDate = DateUtils.getInitDateTime(yesterdayDate);
		Date yesterdayEndDate = DateUtils.getEndDateTime(yesterdayDate);
		logger.info("Consultando recargas del día :  " + yesterdayInitDate + "-" + yesterdayEndDate);
		List<ConektaOrderEvent> conektaOrderDetailList = conektaOrderEventRepository.findByCreatedAtBetweenAndEventType(yesterdayInitDate, yesterdayEndDate, ConektaEventConstants.ORDER_PAID);
		for (ConektaOrderEvent cnektaSubscriptionDetail : conektaOrderDetailList) {
			OdooInvoicing odooInvoicing = new OdooInvoicing();
			ConektaOrder conektaOrder =cnektaSubscriptionDetail.getConektaOrder();
				logger.info("Offerid: " + conektaOrder.getAltanOfferId());
					Integer amount = conektaOrder.getAmount();
					if (ivaPercentage != null) {
						Double amountWithoutTaxes = (amount ) / ((100 / 100.00) + (ivaPercentage / 100.00));
						odooInvoicing.setSubtotal(String.valueOf(String.format("%.2f", amountWithoutTaxes)));
					}
					odooInvoicing.setTotal(String.valueOf(amount ));
			odooInvoicing.setCommercial(EXPRESS_PLUS_RECHARGE_UPER);
			odooInvoicing
					.setCustomerName(EXPRESS_PLUS_RECHARGE_UPER);
			odooInvoicing.setDebt("");
			odooInvoicing.setExpirationDate("");
			odooInvoicing.setInvoiceDate(DateUtils.formatDate(new Date(), DateUtils.YYYYMMDD_FORMAT));
			odooInvoicing.setInvoiceStatus("Factura no generada");
			odooInvoicing.setInvoiceIcon("");
			odooInvoicing.setNumber("");//
			odooInvoicing.setOrigin("EXPRESS_PLUS_RECHARGE_UPER");
			odooInvoicing.setPayments("");
			odooInvoicing.setStatus("Pagado");
			odooInvoicing.setOfferId(conektaOrder.getAltanOfferId());
			odooInvoicing.setCellphoneNumber(conektaOrder.getPhone());

			odooInvoicingList.add(odooInvoicing);
		}
		try {
			invoicingService.createInvoiceExcel(odooInvoicingList, EXPRESS_PLUS_RECHARGE);
		} catch (Exception e) {
			logger.error("No se pudo generar el excel de facturacion para recargas diarias "
					+ e.getMessage());
		}
		return conektaOrderDetailList;
	}
	
}
