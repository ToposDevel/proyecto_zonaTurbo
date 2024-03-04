package com.topostechnology.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.topostechnology.constant.CoverageConstants;
import com.topostechnology.exception.TrException;
import com.topostechnology.model.ValidateCoverageModel;
import com.topostechnology.service.CoverageService;

@Controller
@RequestMapping("/coverage")
public class CoverageController {

	private static final Logger logger = LoggerFactory.getLogger(CoverageController.class);

	@Autowired
	private CoverageService coverageService;

	@Value("${callcenter.phone}")
	private String callCenterPhone;

	@RequestMapping("/")
	public ModelAndView coverage() {
		logger.info("Entrando a validar covertura");
		ValidateCoverageModel validateCoverageModel = new ValidateCoverageModel();
		validateCoverageModel.setCallcenterPhone(callCenterPhone);
		validateCoverageModel.setOrigin(CoverageConstants.COVERAGE_ORIGIN);
		ModelAndView modelAndView = new ModelAndView("coverage/validateCoverage");
		modelAndView.addObject("validateCoverageForm", validateCoverageModel);
		return modelAndView;
	}

	@RequestMapping(value = "/validateCoverage", method = RequestMethod.POST)
	public ModelAndView validateCoverage(ModelAndView modelAndView,
			@Valid @ModelAttribute("validateCoverageForm") final ValidateCoverageModel validateCoverageModel,
			BindingResult bindingResult, HttpServletRequest request, Errors errors) {
		logger.info("Validando covertura");
		try {
			String fieldValidationMessage = coverageService.validateFields(validateCoverageModel);
			if (fieldValidationMessage == null) {
				coverageService.validateCoverage(validateCoverageModel);
			} else {
				bindingResult.reject("failedMessage", fieldValidationMessage);
			}
		} catch (TrException e) {
			bindingResult.reject("failedMessage", e.getMessage());
		} catch (Exception e) {
			logger.error("Error on validateCoverage ", e);
			bindingResult.reject("failedMessage", "La operación no pudo ser realizada, intente nuevamente más tarde");
		}
		validateCoverageModel.setCoverageMessage(CoverageConstants.COVERAGE_MESSAGE);
		validateCoverageModel.setCallcenterPhone(callCenterPhone);
		modelAndView = new ModelAndView("coverage/validateCoverage");
		modelAndView.addObject("validateCoverageForm", validateCoverageModel);
		return modelAndView;
	}
	
	@RequestMapping("/coppel/")
	public ModelAndView coverageCoppel() {
		logger.info("Entrando a validar covertura");
		ValidateCoverageModel validateCoverageModel = new ValidateCoverageModel();
		validateCoverageModel.setCallcenterPhone(callCenterPhone);
		validateCoverageModel.setOrigin(CoverageConstants.COVERAGE_COPPEL_ORIGIN);
		ModelAndView modelAndView = new ModelAndView("coverage/validateCoverage");
		modelAndView.addObject("validateCoverageForm", validateCoverageModel);
		return modelAndView;
	}

}
