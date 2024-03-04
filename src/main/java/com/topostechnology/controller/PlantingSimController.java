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
import com.topostechnology.model.ActivateSimModel;
import com.topostechnology.model.ValidateImeiAndActivateSimModel;
import com.topostechnology.model.WinSimSurveyModel;
import com.topostechnology.service.PlantingSimService;
import com.topostechnology.utils.StringUtils;

@Controller
public class PlantingSimController {

	private static final Logger logger = LoggerFactory.getLogger(PlantingSimController.class);

	@Autowired
	private PlantingSimService plantingSimService;

	@RequestMapping("/promoSim/")
	public ModelAndView load(Model model, Principal principal) throws TrException {
		logger.info("Entrando a promoSim");
		ModelAndView modelAndView = new ModelAndView("planting-sim/winSimSurvey");
		try {
			WinSimSurveyModel winSimSurveyModel = new WinSimSurveyModel();
			modelAndView.addObject("winSimSurveyModel", winSimSurveyModel);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new TrException("La operación no pudo ser realizada, intente nuevamente más tarde");
		}
		return modelAndView;
	}

	@RequestMapping(value = "/promoSim/validateImei/", method = RequestMethod.POST)
	public ModelAndView participate(ModelAndView modelAndView,
			@Valid @ModelAttribute("winSimSurveyModel") final WinSimSurveyModel winSimSurveyModel,
			BindingResult bindingResult, HttpServletRequest request, Errors errors) {
		logger.info("Validando imei " + winSimSurveyModel.getImei());
		modelAndView = new ModelAndView("planting-sim/winSimSurvey");
		bindingResult = plantingSimService.validateWinSimSurveyData(winSimSurveyModel, bindingResult);
		if (!bindingResult.hasFieldErrors()) {
			try {
				String message = plantingSimService.proccessSurvey(winSimSurveyModel);
				plantingSimService.cleanWinSimSurveyData(winSimSurveyModel);
				modelAndView.addObject("confirmationMessage", message);
			} catch (TrException e) {
				bindingResult.reject("failedMessage", e.getMessage());
			} catch (Exception e) {
				logger.error("Error on proccessSurvey.", e);
				bindingResult.reject("failedMessage",
						"La operación no pudo ser realizada, intente nuevamente más tarde");
			}
		}
		return modelAndView;
	}
	
	@RequestMapping("/promoSim/activateSim/")
	public ModelAndView loadActivateSim(Model model, Principal principal) throws TrException {
		logger.info("Entrando a activateSim VIEW");
		ModelAndView modelAndView = new ModelAndView("planting-sim/activateSim");
		try {
			ActivateSimModel activateSimModel = new ActivateSimModel();
			modelAndView.addObject("activateSimModel", activateSimModel);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new TrException("La operación no pudo ser realizada, intente nuevamente más tarde");
		}
		return modelAndView;
	}
	
	@RequestMapping(value = "/promoSim/activateSim/", method = RequestMethod.POST)
	public ModelAndView activateSim(ModelAndView modelAndView, @Valid @ModelAttribute("activateSimModel") final ActivateSimModel  activateSimModel,
			BindingResult bindingResult, HttpServletRequest request, Errors errors) {
		logger.info("Activando sim con iccid " + activateSimModel.getIccid());
		String message = "";
		modelAndView = new ModelAndView("planting-sim/activateSim");
		bindingResult = plantingSimService.validateActivateSimData(activateSimModel, bindingResult);
		if (!bindingResult.hasFieldErrors()) {
			try {
				plantingSimService.activateSim(activateSimModel);
				String responseMsg = plantingSimService.getActivateSimMessage(activateSimModel);
				if(StringUtils.isNotBlank(responseMsg)) {
					message = responseMsg;
					plantingSimService.cleanActivateSimData(activateSimModel);
				} else {
					throw new TrException("No se pudo activar el sim con número iccid " +  activateSimModel.getIccid());
				}
				modelAndView.addObject("confirmationMessage", message);
			} catch (TrException e) {
				bindingResult.reject("failedMessage", e.getMessage());
			} catch (Exception e) {
				logger.error("Error on activate sim." + e.getMessage());
				bindingResult.reject("failedMessage",
						"El sim no pudo ser activado, contacte al área de soporte.");
			}
		}
		return modelAndView;
	}
	
	@RequestMapping("/promoSim/validateImeiAndActivateSim/")
	public ModelAndView validateImeiAndActivateSimLoad(Model model, Principal principal) throws TrException {
		logger.info("Entrando a validateImeiAndActivateSim VIEW");
		ModelAndView modelAndView = new ModelAndView("planting-sim/validateImeiAndActivateSim");
		try {
			ValidateImeiAndActivateSimModel validateImeiAndActivateSimModel = new ValidateImeiAndActivateSimModel();
			modelAndView.addObject("validateImeiAndActivateSimModel", validateImeiAndActivateSimModel);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new TrException("La operación no pudo ser realizada, contacte al área de soporte.");
		}
		return modelAndView;
	}
	
	@RequestMapping(value = "/promoSim/validateImeiAndActivateSim/",  method = RequestMethod.POST)
	public ModelAndView validateImeiAndActivateSim(ModelAndView modelAndView, @Valid @ModelAttribute("validateImeiAndActivateSimModel") final ValidateImeiAndActivateSimModel  validateImeiAndActivateSimModel,
			BindingResult bindingResult, HttpServletRequest request, Errors errors) {
		logger.info("ejecutando validateImeiAndActivateSim para imei " + validateImeiAndActivateSimModel.getImei() );
		modelAndView = new ModelAndView("planting-sim/validateImeiAndActivateSim");
		bindingResult = plantingSimService.validateImeiAndActivateSimData(validateImeiAndActivateSimModel, bindingResult);
		String message = "";
		if (!bindingResult.hasFieldErrors()) {
			try {
				message = plantingSimService.validateImeiAndActivateSim(validateImeiAndActivateSimModel);
				modelAndView.addObject("confirmationMessage", message);
			} catch (TrException e) {
				bindingResult.reject("failedMessage", e.getMessage());
			} catch (Exception e) {
				logger.error("Error on activate sim." + e.getMessage());
				bindingResult.reject("failedMessage",
						"El sim no pudo ser activado, contacte al área de soporte.");
			}
		}
		return modelAndView;
	}

}
