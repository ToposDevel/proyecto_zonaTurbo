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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.topostechnology.exception.TrException;
import com.topostechnology.model.PortingModel;
import com.topostechnology.service.PortingService;
import com.topostechnology.utils.DateUtils;
import com.topostechnology.utils.StringUtils;

@Controller
public class PortingController {
	
	@Autowired
	private PortingService portingService;
	
	private static final Logger logger = LoggerFactory.getLogger(PortingController.class);
	
	@RequestMapping("/porting/")
	public ModelAndView load(Model model, Principal principal) {
		logger.info("Entrando a vista de portabilidad");
		PortingModel portingModel = new PortingModel();
		ModelAndView modelAndView = new ModelAndView("porting/porting");
		modelAndView.addObject("portingForm", portingModel);
		return modelAndView;
	}
	
	@RequestMapping(value = "/porting/", method = RequestMethod.POST)
	public ModelAndView porting(ModelAndView modelAndView,
			@Valid @ModelAttribute("portingForm") final PortingModel portingModel,
			BindingResult bindingResult, HttpServletRequest request, Errors errors) {
		logger.info("Empezando a portar el número " + portingModel.getMsisdnPorted());
		modelAndView = new ModelAndView("porting/porting");
		bindingResult = portingService.validatePortingData(portingModel, bindingResult);
		String message ="";
		if (!bindingResult.hasFieldErrors()) {
			try {
				message = portingService.porting(portingModel);
				portingService.cleanPortingData(portingModel);
				modelAndView.addObject("confirmationMessage", message);
			} catch (TrException e) {
				bindingResult.reject("failedMessage", e.getMessage());
			} catch (Exception e) {
				logger.error("Error on porting.", e.getMessage());
				bindingResult.reject("failedMessage",
						"La operación no pudo ser realizada, intente nuevamente más tarde");
			}
		}
		return modelAndView;
	}
	
	
	@RequestMapping("/porting/status/")
	public ModelAndView getStatus(Model model, Principal principal) {
		logger.info("Entrando a la pantalla de estatus de portabilidad ");
		PortingModel portingModel = new PortingModel();
		ModelAndView modelAndView = new ModelAndView("porting/portingStatus");
		modelAndView.addObject("portingForm", portingModel);
		return modelAndView;
	}
	
	@RequestMapping(value = "/porting/status/", method = RequestMethod.POST)
	public ModelAndView getStatus(ModelAndView modelAndView,
			@Valid @ModelAttribute("portingForm") final PortingModel portingModel,
			BindingResult bindingResult, HttpServletRequest request, Errors errors) {
		logger.info("Consultando el estatus de la potabilidad del número " + portingModel.getMsisdnPorted());
		modelAndView = new ModelAndView("porting/portingStatus");
		bindingResult = portingService.validateCellphoneNumber(portingModel, bindingResult);
		String message="";
		if (!bindingResult.hasFieldErrors()) {
			try {
				portingService.getPortingStatus(portingModel);
				String portingMessage = portingModel.getMessage();
				String cellphoneNumber = "Numero celular: " + portingModel.getMsisdnPorted();
				modelAndView.addObject("cellphoneNumber", cellphoneNumber);
				String userName = "Usuario: " + portingModel.getFullName();
				modelAndView.addObject("userName", userName);
				String iccid = "Iccid: " + portingModel.getIccid();
				modelAndView.addObject("iccid", iccid);
				String statusMessage= StringUtils.isBlank(portingMessage) ? "" :(   "Estatus: " +portingMessage) ;
				modelAndView.addObject("portingStatusMessage", statusMessage);
				if(StringUtils.isNotBlank(portingModel.getPortingDate())) {
					Date portingDate = DateUtils.stringToDate(DateUtils.YYYYMMDDHHMMSS, portingModel.getPortingDate());
					String formattedDate = DateUtils.getFormattedDate(portingDate, DateUtils.DD_MM_YYY_HH_MM_SS);
					String portingDatemessage = "Fecha de portabilidad " + formattedDate;
					modelAndView.addObject("portingDate", portingDatemessage);
					message = message + portingDatemessage;
				}
				logger.info("Status del número celular " + portingModel.getMsisdnTransitory());
				portingService.cleanPortingData(portingModel);
				
			} catch (TrException e) {
				bindingResult.reject("failedMessage", e.getMessage());
			} catch (Exception e) {
				logger.error("Error on porting.", e);
				bindingResult.reject("failedMessage",
						"La operación no pudo ser realizada, intente nuevamente más tarde");
			}
		}
		return modelAndView;
	}
	

}
