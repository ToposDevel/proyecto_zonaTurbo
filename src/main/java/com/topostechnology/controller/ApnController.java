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

import com.topostechnology.exception.TrException;
import com.topostechnology.model.ApnChangeModel;
import com.topostechnology.service.ApnService;

@Controller
public class ApnController {

	@Autowired
	private ApnService apnService;

	private static final Logger logger = LoggerFactory.getLogger(PortingController.class);

	@RequestMapping("/apnChange/")
	public ModelAndView load(Model model, Principal principal) {
		logger.info("Entrando a vista de compatibilidad");
		ApnChangeModel apnChangeModel = new ApnChangeModel();
		ModelAndView modelAndView = new ModelAndView("apn/apnChange");
		modelAndView.addObject("apnChangeForm", apnChangeModel);
		return modelAndView;
	}

	@RequestMapping(value = "/apnChange/", method = RequestMethod.POST)
	public ModelAndView validate(ModelAndView modelAndView,
			@Valid @ModelAttribute("apnChangeForm") final ApnChangeModel apnChangeModel, BindingResult bindingResult,
			HttpServletRequest request, Errors errors) {
		logger.info("Enviando apn change " + apnChangeModel.getCellphoneNumber());
		modelAndView = new ModelAndView("apn/apnChange");
		if (!bindingResult.hasFieldErrors()) {
			try {
				apnService.changeApn(apnChangeModel.getCellphoneNumber());
				modelAndView.addObject("confirmationMessage",
						"En breve recibirás un SMS con las configuraciones necesarias para que puedas navegar con tu equipo y disfrutes de la mejor conexión");
			} catch (TrException e) {
				logger.error("Error on apnChange.", e.getMessage());
				modelAndView.addObject("failedMessage",
						"La operación no pudo ser realizada, intente nuevamente más tarde");
			} catch (Exception e) {
				logger.error("Error on apnChange.", e.getMessage());
				bindingResult.reject("failedMessage",
						"La operación no pudo ser realizada, intente nuevamente más tarde");
				modelAndView.addObject("failedMessage",
						"La operación no pudo ser realizada, intente nuevamente más tarde");
			}
		}
		modelAndView.addObject("apnChangeForm", apnChangeModel);
		return modelAndView;
	}

}
