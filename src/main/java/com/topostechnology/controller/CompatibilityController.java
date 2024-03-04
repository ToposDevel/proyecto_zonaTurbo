package com.topostechnology.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.topostechnology.model.CompatibilityModel;
import com.topostechnology.service.CompatibilityService;

@Controller
public class CompatibilityController {
	
	private static final String COMPATIBLE = "¡Felicidades! Tu telefono es compatible con la nueva red.";
	private static final String COMPATIBLE_VOZAPP = "Felicidades, tu celular es compatible con la nueva red,"
			+ "para hacer uso de voz sobre LTE solo descarga la aplicación VozApp";
	private static final String NOT_FOUND = "No encontramos información, pero no te preocupes "
			+ "comunicate con nosotros y te ayudaremos a encontrar una solución.";
	
	@Autowired
	private CompatibilityService compatibilityService;

private static final Logger logger = LoggerFactory.getLogger(PortingController.class);
	
	@RequestMapping("/compatibility/")
	public ModelAndView load(Model model, Principal principal) {
		logger.info("Entrando a vista de compatibilidad");
		CompatibilityModel compatibilityModel = new CompatibilityModel();
		ModelAndView modelAndView = new ModelAndView("compatibility/compatibility");
		modelAndView.addObject("compatibilityForm", compatibilityModel);
		return modelAndView;
	}
	
	@RequestMapping(value = "/compatibility/", method = RequestMethod.POST)
	public ModelAndView validate(ModelAndView modelAndView,
			@Valid @ModelAttribute("compatibilityForm") final CompatibilityModel compatibilityModel,
			BindingResult bindingResult, HttpServletRequest request, Errors errors) {

		logger.info("Verificando la compatibilidad del imei POST " + compatibilityModel.getImei());

		modelAndView = new ModelAndView("compatibility/compatibility");
		compatibilityService.validateImei(compatibilityModel, bindingResult);
		CompatibilityModel compatibilityModelNew = null;
		if (!bindingResult.hasFieldErrors()) {
			try {
				compatibilityModelNew = compatibilityService.checkImei(compatibilityModel.getImei());
				String message = compatibilityModelNew.getMessage();
				// logger.info("Mensaje a devolver " + message);
				if(message.equals(COMPATIBLE_VOZAPP)) {
					modelAndView.addObject("warningMessage", COMPATIBLE_VOZAPP);
				} else if (message.equals(COMPATIBLE)) {
					modelAndView.addObject("confirmationMessage", message);
				} else if (message.equals(NOT_FOUND)) {
					modelAndView.addObject("notFoundMessage", message);
				} else {
					modelAndView.addObject("failedMessage", message);
				}
			} catch (Exception e) {
				logger.error("Error on validate compatibility.", e.getMessage());
				bindingResult.reject("failedMessage",
						"La operación no pudo ser realizada, intente nuevamente más tarde");
				modelAndView.addObject("failedMessage", e.getMessage());
			}
		}
		modelAndView.addObject("compatibilityForm", compatibilityModelNew);
		return modelAndView;
	}
	
}

