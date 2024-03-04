package com.topostechnology.controller;

import java.security.Principal;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.topostechnology.constant.UserConstants;
import com.topostechnology.exception.TrException;
import com.topostechnology.model.PaymentModel;
import com.topostechnology.model.ValidUserPlans;
import com.topostechnology.service.ConektaPaymentService;
import com.topostechnology.service.ExpressRechargeService;
import com.topostechnology.validation.GeneralValidator;

@Controller
public class ExpressRechargeController {
	
	@Autowired
	private ExpressRechargeService expressRechargeService;
	
	@Autowired
	private ConektaPaymentService conektaPaymentService;
	

	private static final Logger logger = LoggerFactory.getLogger(ExpressRechargeController.class);
	
	@Value("${erp.recharge.ws}")
	private String erpUrl;
	
//	@RequestMapping("/recharge/expressRecharge/")
	public ModelAndView load(Model model, Principal principal) {
		logger.info("Entrando a vista de recarga express");
		PaymentModel rechargeModel = new PaymentModel();
		ModelAndView modelAndView = new ModelAndView("recharge/expressRecharge");
		modelAndView.addObject("expressRechargeForm", rechargeModel);
		return modelAndView;
	}
	
//	@RequestMapping(value = "/recharge/expressRecharge/plans/{celphoneNumber}", method = RequestMethod.GET)
	public @ResponseBody ValidUserPlans getValidUserPlans(@PathVariable("celphoneNumber") String celphoneNumber)
			throws TrException {
		logger.info("Consultando ofertas validas para " + celphoneNumber);
		ValidUserPlans validUserPlans = new ValidUserPlans();
		if (GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER, celphoneNumber)) {
			try {
				validUserPlans = expressRechargeService.getUserPlans(celphoneNumber);
				if(validUserPlans.getPlanList() == null || validUserPlans.getPlanList().isEmpty()) {
					throw new TrException("No se encontraron planes diponibles para recarga, por favor comunícate a nuestro Call Center." );
				}
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
	
//	@RequestMapping(value = "/recharge/expressRecharge/", method = RequestMethod.POST) // COMO FUNCIONA AHORITA
	public ModelAndView porting(ModelAndView modelAndView,
			@Valid @ModelAttribute("expressRechargeForm") final PaymentModel paymentModel,
			BindingResult bindingResult, HttpServletRequest request, Errors errors)  {
		logger.info("redireccionando a recarga de oddu para la oferta " + paymentModel.getPlanSelectedId());
		modelAndView = new ModelAndView("recharge/expressRecharge");
		bindingResult = conektaPaymentService.validateBasicFormData(paymentModel, bindingResult);
		if (!bindingResult.hasFieldErrors()) {
			logger.info("Consultando url para oferta " + paymentModel.getPlanSelectedId() );
			String odooOffername   = expressRechargeService.getRechargeUrl(paymentModel); // se consulta el link(odoo) para la recarga
			if(odooOffername != null) {
				String urlTorRecharge =  erpUrl + paymentModel.getCellphoneNumber() + "/" + odooOffername;
				String url = urlTorRecharge.replaceAll("\\r", "").replace("\\n", "");
				logger.info("Redirect to url: " + url);
				modelAndView = new ModelAndView("redirect:" + url);
			} else {
				logger.error("No se encontro url de odoo para esta oferta");
				bindingResult.reject("failedMessage", "El plan seleccionado no se encuentra disponible para recarga desde este sitio.");
			}
			expressRechargeService.cleanFormData(paymentModel);
		}else {
    		for (Object object : bindingResult.getAllErrors()) {
    		    if(object instanceof FieldError) {
    		        FieldError fieldError = (FieldError) object;
    		        logger.error(fieldError.getCode());
    		    }
    		    if(object instanceof ObjectError) {
    		        ObjectError objectError = (ObjectError) object;
    		        logger.error(objectError.getCode());
    		    }
    		}
    	}
		return modelAndView;
	}
	
	
}
