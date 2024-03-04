package com.topostechnology.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.topostechnology.constant.PaymentConstants;
import com.topostechnology.exception.TrException;
import com.topostechnology.model.CardPaymentModel;
import com.topostechnology.model.PaymentModel;
import com.topostechnology.service.ConektaCardService;
import com.topostechnology.service.ConektaOxxoPayService;
import com.topostechnology.service.ConektaPaymentService;
import com.topostechnology.service.OpenpayService;
import com.topostechnology.utils.StringUtils;

import io.conekta.Error;
import io.conekta.ErrorList;

@Controller
@Validated
public class PaymentController {

	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

	@Autowired
	private ConektaOxxoPayService conektaOxxoPayService;
	
	@Autowired
	private ConektaCardService conektaCardService;
	
	@Autowired
	private ConektaPaymentService conektaPaymentService;
	
	@Autowired
	private OpenpayService openpayService;
	
	@RequestMapping(value = "/conekta/payment/paymentMethods/",  method = RequestMethod.POST)
	public ModelAndView redirectToPaymentMethod(ModelAndView modelAndView,
			@Valid @ModelAttribute("paymentForm") final PaymentModel paymentModel,
			BindingResult bindingResult, HttpServletRequest request, Errors errors)  {
		logger.info("Redireccionando a pago seleccionado");
		try {
			modelAndView = new ModelAndView("conekta/payment/paymentMethods");
			bindingResult = conektaPaymentService.validateFormDataForPayment(paymentModel, bindingResult);
			if (!bindingResult.hasFieldErrors()) {
				String paymentMethod = paymentModel.getPaymentMethod(); // método de págo seleccionado
				switch(paymentMethod) {
				case PaymentConstants.OXXO_PAYMENT_METHOD:
					// GENERA REFERENCIA PARA IR A PAGAR A OXXO
					String oxxoReference;
					oxxoReference = conektaOxxoPayService.generateReference(paymentModel);
					if(StringUtils.isNotBlank(oxxoReference)) {
						logger.info("Redireccionando a link de pago " + paymentMethod);
						modelAndView = new ModelAndView("conekta/payment/opps_es");
						modelAndView.addObject("oxooReference", oxxoReference);
						modelAndView.addObject("amount", paymentModel.getAmount());
					} else {
						modelAndView.addObject("failedMessage", "Se ha generado un error al generar la referencia Oxxo pay, intente nuevamente");
					}
					break;
				case PaymentConstants.CARD_CONEKTA_PAYMENT_METHOD:
					// REDIRECCIONA A PANTALLA DE PAGO CON TARJETA DE CREDITO
					modelAndView = new ModelAndView("conekta/payment/cardPayment");
					CardPaymentModel cardPaymentModel = conektaCardService.createCardPaymentModel(paymentModel);
					modelAndView.addObject("cardPaymentModel", cardPaymentModel);
					break;
				case PaymentConstants.PAYNET_PAYMENT_METHOD:
					String paymentUrl = openpayService.CreateOrder(paymentModel);
					modelAndView = new ModelAndView("redirect:" + paymentUrl);
					break;
				case PaymentConstants.COPPEL_PAY_METHOD:
					String paymentCoppelPayUrl = "https://turbopos.turbored.org/coppelfroentend/solicita-compra/"+paymentModel.getPlanSelectedId()+"/"+paymentModel.getCellphoneNumber(); 
					modelAndView = new ModelAndView("redirect:" + paymentCoppelPayUrl);
					break;
				default:
					throw new TrException("Método de pago no permitido.");
				}

			}else {
	    		for (Object object : bindingResult.getAllErrors()) {
	    		    if(object instanceof FieldError) {
	    		        FieldError fieldError = (FieldError) object;
	    		        logger.info(fieldError.getField());
	    		        logger.error(fieldError.getCode());
	    		    }
	    		    if(object instanceof ObjectError) {
	    		        ObjectError objectError = (ObjectError) object;
	    		        logger.info(objectError.getObjectName());
	    		        logger.error(objectError.getCode());
	    		    }
	    		}
	    		modelAndView.addObject("failedMessage", "La operación no pudo ser realizada, intente nuevamente más tarde.");
	    	}
		} catch(TrException ex){
			modelAndView.addObject("failedMessage", ex.getMessage());
		} catch(Exception ex){
			modelAndView.addObject("failedMessage", "La operación no pudo ser realizada, intente nuevamente más tarde.");
		}
		return modelAndView;
	}
	
	@RequestMapping(value = "/conekta/cardPayment/pay",  method = RequestMethod.POST)
	public ModelAndView creditCardPay(ModelAndView modelAndView,
			@Valid @ModelAttribute("subscriptionModel") final CardPaymentModel cardPaymentModel,
			BindingResult bindingResult, HttpServletRequest request, Errors errors) {
		logger.info("ejecutando pago con conekta  " + cardPaymentModel.getCardTitularName() + "-"
				+ cardPaymentModel.getCellphoneNumber());
		bindingResult = conektaCardService.validateCardPaymentData(cardPaymentModel, bindingResult);
		modelAndView = new ModelAndView("conekta/payment/paymentResponse");
		if (!bindingResult.hasFieldErrors()) {
			try {
				conektaCardService.createOrder(cardPaymentModel);
				modelAndView.addObject("confirmationMessage", "La orden esta siendo procesada, en breve recibirás un correo para ser informado sobre el pago.");
			} catch (ErrorList e) {
				String errorStr = "";
				ArrayList<Error> details = e.details;
				for (io.conekta.Error error : details) {
					errorStr = errorStr + " " + error.getMessage();
				}
				logger.error("El pago con tarjeta no pudo ser procesado. error: " + errorStr);
				modelAndView.addObject("failedMessage", "El pago con tarjeta no pudo ser procesado.");
			} catch (Exception e) {
				logger.error("creditCardPay El pago con tarjeta no pudo ser procesado. error: " + e);
				modelAndView.addObject("failedMessage", "El pago con tarjeta no pudo ser procesado.");
			}
		} else {
			modelAndView.addObject("failedMessage",
					"La operación no pudo ser completada, verifique sus datos e intente nuevamente.");
			for (Object object : bindingResult.getAllErrors()) {
				if (object instanceof FieldError) {
					FieldError fieldError = (FieldError) object;
					logger.error(fieldError.getCode());
				}
				if (object instanceof ObjectError) {
					ObjectError objectError = (ObjectError) object;
					logger.error(objectError.getCode());
				}
			}
		}
		return modelAndView;
	}
	
	
	@RequestMapping(value = "/conekta/payment/paymentMethodOxxoPaynet/",  method = RequestMethod.POST)
	public ModelAndView redirectToPaymentMethodOxxo(ModelAndView modelAndView,
												@Valid @ModelAttribute("expressRechargeForm") final PaymentModel paymentModel,
												BindingResult bindingResult, HttpServletRequest request, Errors errors)  {
		logger.info("Redireccionando a pago seleccionado");
		try {
			modelAndView = new ModelAndView("recharge/expressPlusRechargeMovOxxoPaynet");
			bindingResult = conektaPaymentService.validateFormDataForPaymentOxxoPaynet(paymentModel, bindingResult);
			if (!bindingResult.hasFieldErrors()) {
				String paymentMethod = paymentModel.getPaymentMethod(); // método de págo seleccionado
				logger.info("Payment " +  paymentModel.getPaymentMethod());
				logger.info("Plan " +  paymentModel.getPlanSelectedId());
				logger.info("Celular " +  paymentModel.getCellphoneNumber());
				logger.info("Amount " +  paymentModel.getAmount());
				logger.info("PlanName " +  paymentModel.getPlanSelectedName());
				logger.info("Email " +  paymentModel.getEmail());


			 switch(paymentMethod) {
					case PaymentConstants.OXXO_PAYMENT_METHOD:
						// GENERA REFERENCIA PARA IR A PAGAR A OXXO
						String oxxoReference;
						oxxoReference = conektaOxxoPayService.generateReference(paymentModel);
						if(StringUtils.isNotBlank(oxxoReference)) {
							logger.info("Redireccionando a link de pago " + paymentMethod);
							modelAndView = new ModelAndView("conekta/payment/opps_es");
							modelAndView.addObject("oxooReference", oxxoReference);
							modelAndView.addObject("amount", paymentModel.getAmount());
						} else {
							modelAndView.addObject("failedMessage", "Se ha generado un error al generar la referencia Oxxo pay, intente nuevamente");
						}
						break;

					case PaymentConstants.PAYNET_PAYMENT_METHOD:
						String paymentUrl = openpayService.CreateOrder(paymentModel);
						modelAndView = new ModelAndView("redirect:" + paymentUrl);
						break;
					case PaymentConstants.CARD_CONEKTA_PAYMENT_METHOD:
						 // REDIRECCIONA A PANTALLA DE PAGO CON TARJETA DE CREDITO
						 modelAndView = new ModelAndView("conekta/payment/cardPayment");
						 CardPaymentModel cardPaymentModel = conektaCardService.createCardPaymentModel(paymentModel);
						 modelAndView.addObject("cardPaymentModel", cardPaymentModel);
						 break;
					case PaymentConstants.COPPEL_PAY_METHOD:
						String paymentCoppelPayUrl = "https://turbopos.turbored.org/coppelfroentend/solicita-compra/"+paymentModel.getPlanSelectedId()+"/"+paymentModel.getCellphoneNumber(); 
						modelAndView = new ModelAndView("redirect:" + paymentCoppelPayUrl);
						break;
					
					default:
						throw new TrException("Método de pago no permitido.");
				}

			}else {
				for (Object object : bindingResult.getAllErrors()) {
					if(object instanceof FieldError) {
						FieldError fieldError = (FieldError) object;
						logger.info(fieldError.getField());
						logger.error(fieldError.getCode());
					}
					if(object instanceof ObjectError) {
						ObjectError objectError = (ObjectError) object;
						logger.info(objectError.getObjectName());
						logger.error(objectError.getCode());
					}
				}
				modelAndView.addObject("failedMessage", "La operación no pudo ser realizada, intente nuevamente más tarde.");
			}
		} /*catch(TrException ex){
			modelAndView.addObject("failedMessage", ex.getMessage());
		} */catch(Exception ex){
			System.out.println("ERROR: "+ex.getMessage());
			modelAndView.addObject("failedMessage", "La operación no pudo ser realizada, intente nuevamente más tarde.");
		}
		return modelAndView;
	}


	
}
