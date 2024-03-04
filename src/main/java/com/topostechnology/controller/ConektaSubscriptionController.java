package com.topostechnology.controller;

import java.security.Principal;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.topostechnology.constant.UserConstants;
import com.topostechnology.exception.TrException;
import com.topostechnology.model.SubscriptionModel;
import com.topostechnology.model.ValidUserPlans;
import com.topostechnology.service.ConektaSubscriptionService;
import com.topostechnology.service.CoverageService;
import com.topostechnology.service.ProfileService;
import com.topostechnology.validation.GeneralValidator;

import io.conekta.Error;
import io.conekta.ErrorList;

@Controller
@Validated
@CrossOrigin(origins = "http://zonaturbo.turbored.com/", methods= {RequestMethod.GET,RequestMethod.POST})
public class ConektaSubscriptionController {

	private static final Logger logger = LoggerFactory.getLogger(ConektaSubscriptionController.class);

	@Autowired
	private ConektaSubscriptionService conektaService;

	@Autowired
	private ProfileService profileService;

	@Autowired
	private CoverageService coverageService;
	
	@Value("${callcenter.phone}")
	private String callCenterPhone;
	
	@RequestMapping("/conekta/subscription/cellphoneNumberSubscription/")
	public ModelAndView load(Model model, Principal principal) throws TrException {
		logger.info("Entrando a conekta suscripcion");
		ModelAndView modelAndView = new ModelAndView("conekta/subscription/subscription");
		try {
			SubscriptionModel suscriptionModel = new SubscriptionModel();
			suscriptionModel.setSuscriptionWithImei(false);
			modelAndView.addObject("suscriptionModel", suscriptionModel);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new TrException("La operación no pudo ser realizada, intente nuevamente más tarde");
		}
		return modelAndView;
	}

	@RequestMapping("/conekta/subscription/imeiSubscription/")
	public ModelAndView loadHbbMifiSubscription(Model model, Principal principal) throws TrException {
		logger.info("Entrando a conekta suscripcion");
		ModelAndView modelAndView = new ModelAndView("conekta/subscription/subscription");
		try {
			SubscriptionModel suscriptionModel = new SubscriptionModel();
			suscriptionModel.setSuscriptionWithImei(true);
			modelAndView.addObject("suscriptionModel", suscriptionModel);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new TrException("La operación no pudo ser realizada, intente nuevamente más tarde");
		}
		return modelAndView;
	}

	@RequestMapping(value = "/conekta/subscription/", method = RequestMethod.POST)
	public ModelAndView subscribe(ModelAndView modelAndView,
			@Valid @ModelAttribute("subscriptionModel") final SubscriptionModel suscriptionModel,
			BindingResult bindingResult, HttpServletRequest request, Errors errors) {
		logger.info("ejecutando subscribe " + suscriptionModel.getCardTitularName() + "-"
				+ suscriptionModel.getCellphoneNumber());
		bindingResult = conektaService.validateSubscriptionData(suscriptionModel, bindingResult);
		modelAndView = new ModelAndView("conekta/subscription/subscriptionResponse");
		if (!bindingResult.hasFieldErrors()) {
			try {
				String confirmationMessage = conektaService.subscribe(suscriptionModel);
				modelAndView.addObject("confirmationMessage", confirmationMessage);
				
			} catch (TrException e) {
				modelAndView.addObject("failedMessage", "No pudo completarse el proceso de domiciliación, comunicate a nuestro call center " + callCenterPhone);
			} catch (ErrorList e) {
				String errorStr = "";
				ArrayList<Error> details = e.details;
				for (io.conekta.Error error : details) {
					errorStr = errorStr + " " + error.getMessage();
				}
				logger.error(errorStr);
				modelAndView.addObject("failedMessage",
						"La operación no pudo ser completada, intente nuevamente más tarde.");
			} catch (Exception e) {
				logger.error("Error on subscribe.", e.getMessage());
				bindingResult.reject("failedMessage",
						"La operación no pudo ser completada, intente nuevamente más tarde.");
				modelAndView.addObject("failedMessage",
						"La operación no pudo ser completada, intente nuevamente más tarde.");
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

	@RequestMapping(value = "/conekta/subscription/offersMov/{cellphoneNumber}", method = RequestMethod.GET)
	public @ResponseBody ValidUserPlans getOffersMov(@PathVariable("cellphoneNumber") String cellphoneNumber)
			throws TrException {
		logger.info("Consultando ofertas MOV para el número " + cellphoneNumber);
		ValidUserPlans validUserPlans = new ValidUserPlans();
		if (GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER, cellphoneNumber)) {
			try {
				String supperOfferType =  profileService.getSuperOfferType(cellphoneNumber);
				validUserPlans = conektaService.getValidUserPlans(cellphoneNumber, supperOfferType);
			} catch (TrException e) {
				validUserPlans.setErrorMessage(e.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new TrException("La operación no pudo ser realizada, intente nuevamente más tarde");
			}
		} else {
			String errorMessage = "El número celular debe contener 10 dítitos.";
			validUserPlans.setErrorMessage(errorMessage);
		}
		return validUserPlans;
	}

	@RequestMapping(value = "/conekta/subscription/offersHbbMifi/{imei}", method = RequestMethod.GET)
	public @ResponseBody ValidUserPlans getOffersHbbMifi(@PathVariable("imei") String imei) throws TrException {
		logger.info("Consultando ofertas para el imei " + imei);
		ValidUserPlans validUserPlans = new ValidUserPlans();
		if (GeneralValidator.validatePattern(UserConstants.REGEX_IMEI_NUMBER, imei)) {
			try {
				String cellphoneNumber = profileService.getCellphoneNumberByImei(imei);
				String supperOfferType =  profileService.getSuperOfferType(cellphoneNumber);
				validUserPlans = conektaService.getValidUserPlans(cellphoneNumber, supperOfferType);
			} catch (TrException e) {
				validUserPlans.setErrorMessage(e.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new TrException("La operación no pudo ser realizada, intente nuevamente más tarde");
			}
		} else {
			String errorMessage = "El IMEI debe contener 15 dítitos.";
			validUserPlans.setErrorMessage(errorMessage);
		}
		return validUserPlans;
	}

	@RequestMapping(value = "/conekta/subscription/validateCoverage/{coordinates}", method = RequestMethod.GET)
	public @ResponseBody boolean validateCoverage(@PathVariable("coordinates") String coordinates) throws TrException {
		logger.info("Validando covertura en las coordenadas " + coordinates);
		boolean hasCoverage = false;
		try {
			hasCoverage = coverageService.hasCoverage(coordinates);
		} catch (Exception e) {
			logger.error("Error al validar coordenadas " + e.getMessage());
			throw new TrException("Las coordenadas no pudierons ser validadas, intente nuevamente más tarde");
		}
		logger.info("Con cobertura: " + hasCoverage);
		return hasCoverage;
	}
	
}
