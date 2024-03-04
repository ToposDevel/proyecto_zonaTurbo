package com.topostechnology.controller;

import java.security.Principal;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.topostechnology.constant.PaymentConstants;
import com.topostechnology.domain.BinacleSubscription;
import com.topostechnology.exception.TrException;
import com.topostechnology.model.PaymentModel;
import com.topostechnology.model.ValidUserPlans;
import com.topostechnology.repository.BinacleSubscriptionRepository;
import com.topostechnology.service.ConektaPaymentService;
import com.topostechnology.service.ExpressRechargeService;
import com.topostechnology.service.ProfileService;

@Controller
public class ExpressPlusRechargeController {

	@Autowired
	private ExpressRechargeService expressRechargeService;

	@Autowired
	private ConektaPaymentService conektaPaymentService;
	
	@Autowired
	private ProfileService profileService;
	
	@Autowired
	private BinacleSubscriptionRepository  binacleSubscriptionRepository;

	private static final Logger logger = LoggerFactory.getLogger(ExpressPlusRechargeController.class);
	
	@RequestMapping("/recharge/payment")
	public ModelAndView methosRechargePaymentView(Model model, Principal principal, HttpServletRequest request) {

		PaymentModel rechargeModel = new PaymentModel();
		String expressPlusRechargeMovView = "conekta/payment/paymentMethodsRecharge";
		rechargeModel.setFromViewUrl(expressPlusRechargeMovView);
		ModelAndView modelAndView = new ModelAndView(expressPlusRechargeMovView);
		modelAndView.addObject("paymentForm", rechargeModel);
		return modelAndView;
	}


	@RequestMapping("/recharge/expressPlusRechargeMov")
	public ModelAndView expressPlusRechargeMov(Model model, Principal principal, HttpServletRequest request) {
		logger.info("Entrando a vista de recarga express MOV."); 
		logger.info("REQUEST IP " + request.getRemoteAddr());// TODO QUITAR
		logger.info("REQUEST IP " + request.getRemoteHost()); // TODO QUITAR
		PaymentModel rechargeModel = new PaymentModel();
		String expressPlusRechargeMovView = "recharge/expressPlusRechargeMov";
		rechargeModel.setFromViewUrl(expressPlusRechargeMovView);
		ModelAndView modelAndView = new ModelAndView(expressPlusRechargeMovView);
		modelAndView.addObject("expressRechargeForm", rechargeModel);
		return modelAndView;
	}
	
	@RequestMapping("/recharge/expressPlusRechargeMifi/")
	public ModelAndView expressPlusRechargeMifi(Model model, Principal principal, HttpServletRequest request) {
		logger.info("Entrando a vista de recarga express MIFI");
		logger.info("REQUEST IP " + request.getRemoteAddr());// TODO QUITAR
		logger.info("REQUEST IP " + request.getRemoteHost()); // TODO QUITAR
		PaymentModel rechargeModel = new PaymentModel();
		String expressPlusRechargeMifiUrl = "recharge/expressPlusRechargeMifi";
		rechargeModel.setFromViewUrl(expressPlusRechargeMifiUrl);
		ModelAndView modelAndView = new ModelAndView(expressPlusRechargeMifiUrl);
		modelAndView.addObject("expressRechargeForm", rechargeModel);
		return modelAndView;
	}
	
	@RequestMapping("/recharge/expressPlusRechargeHbb/")
	public ModelAndView expressPlusRechargeHbb(Model model, Principal principal, HttpServletRequest request) {
		logger.info("Entrando a vista de recarga express HBB");
		logger.info("REQUEST IP " + request.getRemoteAddr());// TODO QUITAR
		logger.info("REQUEST IP " + request.getRemoteHost()); // TODO QUITAR
		PaymentModel rechargeModel = new PaymentModel();
		String expressPlusRechargeHbbUrl = "recharge/expressPlusRechargeHbb";
		rechargeModel.setFromViewUrl(expressPlusRechargeHbbUrl);
		ModelAndView modelAndView = new ModelAndView(expressPlusRechargeHbbUrl);
		modelAndView.addObject("expressRechargeForm", rechargeModel);
		return modelAndView;
	}

	@RequestMapping(value = "/recharge/expressPlusRecharge/plans/{celphoneNumberOrImei}", method = RequestMethod.GET)
	public @ResponseBody ValidUserPlans getValidUserPlans(@PathVariable("celphoneNumberOrImei") String cellphoneNumberOrImei)
			throws TrException {
		logger.info("Consultando ofertas validas para " + cellphoneNumberOrImei);
		ValidUserPlans validUserPlans = new ValidUserPlans();
		String errorMessage = expressRechargeService.validateImeiOrCellphoneNumber(cellphoneNumberOrImei);
		if (errorMessage == null) {
			try {
				String cellphoneNumber;
				int cellphoneNumberOrImeiSize = cellphoneNumberOrImei.length();
				if(cellphoneNumberOrImeiSize == 15) {
					cellphoneNumber = profileService.getCellphoneNumberByImei(cellphoneNumberOrImei);
					logger.info("Número celular " + cellphoneNumber + "Para el imei " + cellphoneNumberOrImei);
				} else {
					cellphoneNumber = cellphoneNumberOrImei;
				}
				validUserPlans = expressRechargeService.getUserPlans(cellphoneNumber);
				if (validUserPlans.getPlanList() == null || validUserPlans.getPlanList().isEmpty()) {
					throw new TrException(
							"No se encontraron planes diponibles para recarga, por favor comunícate a nuestro Call Center.");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new TrException("La operación no pudo ser realizada, intente nuevamente más tarde");
			}
		} else {
			validUserPlans.setErrorMessage(errorMessage);
		}
		return validUserPlans;
	}

	@RequestMapping(value = "/recharge/expressPlusRecharge/", method = RequestMethod.POST)
	public ModelAndView selectPaymentMethods(ModelAndView modelAndView,
			@Valid @ModelAttribute("expressRechargeForm") final PaymentModel rechargeModel, BindingResult bindingResult,
			HttpServletRequest request, Errors errors) {
		
		BinacleSubscription binacleSubscription = new BinacleSubscription();
		binacleSubscription.setCreateAt(new Date());
		binacleSubscription.setPhone(rechargeModel.getCellphoneNumber());
		logger.info("redireccionando a opciones de pago  numero celular " + rechargeModel.getCellphoneNumber()
				+ " y  oferta " + rechargeModel.getPlanSelectedId());
		modelAndView = new ModelAndView(rechargeModel.getFromViewUrl());
		bindingResult = conektaPaymentService.validateBasicFormData(rechargeModel, bindingResult);
		if (!bindingResult.hasFieldErrors()) {
			logger.info("mostrar metodo de pago para oferta " + rechargeModel.getPlanSelectedId());
			try {
				binacleSubscription.setInternalStatus("Recarga prueba bitacora para domiciliacion");
				modelAndView = new ModelAndView("conekta/payment/paymentMethods");
				modelAndView.addObject("paymentForm", rechargeModel);
			} catch (Exception e) {
				modelAndView.addObject("failedMessage",
						"la recarga seleccionada no esta disponible por este medio, comunicate al call center.");
			}
		} else {
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
		
		binacleSubscriptionRepository.save(binacleSubscription);
		return modelAndView;
	}
	
	
	@RequestMapping("/recharge/expressPlusRechargeMovOxxo")
	public ModelAndView methosPaymentOxxo(Model model, Principal principal, HttpServletRequest request) {
		System.out.println("Oxxo");
		logger.info("Entrando a vista de recarga express MOV.");
		logger.info("REQUEST IP " + request.getRemoteAddr());// TODO QUITAR
		logger.info("REQUEST IP " + request.getRemoteHost()); // TODO QUITAR
		PaymentModel rechargeModel = new PaymentModel();
		rechargeModel.setPaymentMethod(PaymentConstants.OXXO_PAYMENT_METHOD);
		String expressPlusRechargeMovView = "recharge/expressPlusRechargeMovPaymentMethod";
		rechargeModel.setFromViewUrl(expressPlusRechargeMovView);
		ModelAndView modelAndView = new ModelAndView(expressPlusRechargeMovView);
		modelAndView.addObject("expressRechargeForm", rechargeModel);
		return modelAndView;
	}


	/* Metodo agregado */
	@RequestMapping("/recharge/expressPlusRechargeMovPaynet")
	public ModelAndView methosPaymentPaynet(Model model, Principal principal, HttpServletRequest request) {
		System.out.println("PAynet");
		logger.info("Entrando a vista de recarga express MOV.");
		logger.info("REQUEST IP " + request.getRemoteAddr());// TODO QUITAR
		logger.info("REQUEST IP " + request.getRemoteHost()); // TODO QUITAR
		PaymentModel rechargeModel = new PaymentModel();
		rechargeModel.setPaymentMethod(PaymentConstants.PAYNET_PAYMENT_METHOD);

		System.out.println("objeto"+rechargeModel.getPaymentMethod());
		String expressPlusRechargeMovView = "recharge/expressPlusRechargeMovPaymentMethod";
		rechargeModel.setFromViewUrl(expressPlusRechargeMovView);
		ModelAndView modelAndView = new ModelAndView(expressPlusRechargeMovView);
		modelAndView.addObject("expressRechargeForm", rechargeModel);
		return modelAndView;
	}

	

@RequestMapping(value = "/recharge/expressPlusRechargePayment/",  method = RequestMethod.POST)
	public ModelAndView redirectToPaymentMethod(ModelAndView modelAndView,
												@Valid @ModelAttribute("paymentForm") final PaymentModel paymentModel,
												BindingResult bindingResult, HttpServletRequest request, Errors errors)  {
		logger.info("Redireccionando a pantalla de datos de la recarga seleccionada");
		logger.info("URL"+paymentModel.getFromViewUrl());
		try {
			modelAndView = new ModelAndView(paymentModel.getFromViewUrl());
			bindingResult = conektaPaymentService.validateFormDataForPaymentMethodRecharge(paymentModel, bindingResult);
			if (!bindingResult.hasFieldErrors()) {
				String paymentMethod = paymentModel.getPaymentMethod(); // método de págo seleccionado
				System.out.println("objeto"+paymentMethod);
				String expressPlusRechargeMovView = "recharge/expressPlusRechargeMovPaymentMethod";
				paymentModel.setPaymentMethod(paymentMethod);
				modelAndView = new ModelAndView(expressPlusRechargeMovView);
				modelAndView.addObject("expressRechargeForm", paymentModel);


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

		} catch(Exception ex){
			System.out.println("ERROR: "+ex.getMessage());
			modelAndView.addObject("failedMessage", "La operación no pudo ser realizada, intente nuevamente más tarde.");
		}
		return modelAndView;
	}

}
