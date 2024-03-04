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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.topostechnology.exception.TrException;
import com.topostechnology.model.ImeiPhoneModel;
import com.topostechnology.model.TurboOfficeActivationModel;
import com.topostechnology.model.TurboOfficeRechargeModel;
import com.topostechnology.model.ValidateIdentityModel;
import com.topostechnology.rest.client.response.TurboOfficePlanDto;
import com.topostechnology.service.ProfileService;
import com.topostechnology.service.TurboOfficeService;
import com.topostechnology.utils.StringUtils;

@Controller
public class TurboOfficeController {
	
	private static final Logger logger = LoggerFactory.getLogger(TurboOfficeController.class);
	
	@Autowired
	private TurboOfficeService turboOfficeService;
	
	@Value("${callcenter.phone}")
	private String callCenterPhone;
	
	@Autowired
	private ProfileService profileService;
	
	@RequestMapping("/turboOffice/register")
	public ModelAndView turboOffice(Model model, Principal principal) throws Exception {
		logger.info("Entrando a vista Turbo Office activacion");
		TurboOfficeActivationModel turboOfficeActivationModel = turboOfficeService.getTurboOficePlansInfo();
		ModelAndView modelAndView = new ModelAndView("turbooffice/register");
		modelAndView.addObject("turboOfficeActivationModel", turboOfficeActivationModel);
		return modelAndView;
	}
	
	@RequestMapping(value = "/turboOffice/activation/paymentLink", method = RequestMethod.POST)
	public ModelAndView register(ModelAndView modelAndView,
			@Valid @ModelAttribute("turboOfficeActivationModel") final TurboOfficeActivationModel turboOfficeActivationModel,
			BindingResult bindingResult, HttpServletRequest request, Errors errors) {
		logger.info("Obteniendo link de pago para " + turboOfficeActivationModel.getCellphoneNumber());
		bindingResult = turboOfficeService.validateTurboOfficeForm(turboOfficeActivationModel, bindingResult);
		modelAndView = new ModelAndView("turbooffice/register");
		if (!bindingResult.hasFieldErrors()) {
			try {
				String paymentLink = turboOfficeService.saveTurboUserAndGetPaymentLink(turboOfficeActivationModel);
				turboOfficeActivationModel.setPaymentLink(paymentLink);
				modelAndView.addObject("confirmationMessage", "El link de pago ha sido enviado a su correo electronico.");
			} catch (TrException e) {
				turboOfficeActivationModel.setPaymentLink(null);
				modelAndView.addObject("failedMessage", e.getMessage());
			} catch (Exception e) {
				turboOfficeActivationModel.setPaymentLink(null);
				logger.error("Error on turbo office activation.", e.getMessage() );
				modelAndView.addObject("failedMessage",
						"No pudo generarse el link de pago, comunicate a nuestro call center" + callCenterPhone);
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
	
	@RequestMapping("/turboOffice/extraRecharge")
	public ModelAndView rechargeView(Model model, Principal principal) throws Exception {
		logger.info("Entrando a vista Turbo Office recarga");
		TurboOfficeRechargeModel turboOfficeRechargeModel = new TurboOfficeRechargeModel();
		TurboOfficePlanDto turboOfficePlanDto = turboOfficeService.getTurboOficeRechargeOptions();
		turboOfficeRechargeModel.setTurboOfficePlanName(turboOfficePlanDto.getName());
		turboOfficeRechargeModel.setPrice(turboOfficePlanDto.getPrice());
		turboOfficeRechargeModel.setTurboOfficeRechargeSelected(turboOfficeService.getTurboOficeRechargeOptions());
		turboOfficeRechargeModel.setPrice(turboOfficePlanDto.getPrice());
		ModelAndView modelAndView = new ModelAndView("turbooffice/recharge");
		modelAndView.addObject("turboOfficeRechargeModel", turboOfficeRechargeModel);
		return modelAndView;
	}
	
	@RequestMapping(value = "/turboOffice/extraRecharge", method = RequestMethod.POST)
	public ModelAndView extraRecharge(ModelAndView modelAndView,
			@Valid @ModelAttribute("turboOfficeRechargeModel") final TurboOfficeRechargeModel turboOfficeRechargeModel,
			BindingResult bindingResult, HttpServletRequest request, Errors errors) {
		logger.info("Generando link de pago para recarga " + turboOfficeRechargeModel.getVirtualNumber());
		bindingResult = turboOfficeService.validateExtraRechargeForm(turboOfficeRechargeModel, bindingResult);
		modelAndView = new ModelAndView("turbooffice/recharge");
		if (!bindingResult.hasFieldErrors()) {
			try {
				String paymentLink = turboOfficeService.saveExtraRechargeAndGetPaymentLink(turboOfficeRechargeModel);
				turboOfficeRechargeModel.setPaymentLink(paymentLink);
				modelAndView.addObject("confirmationMessage", "El link de pago ha sido enviado a su correo electronico.");
			} catch (TrException e) {
				turboOfficeRechargeModel.setPaymentLink(null);
				modelAndView.addObject("failedMessage", e.getMessage());
			} catch (Exception e) {
				turboOfficeRechargeModel.setPaymentLink(null);
				logger.error("Error on turbo office recarga adicional.", e.getMessage() );
				modelAndView.addObject("failedMessage",
						"No pudo generarse el link de pago, comunicate a nuestro centro de atencion " + callCenterPhone);
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
	
	@RequestMapping("/turboOffice/phoneImei")
	public ModelAndView phoneImei(Model model, Principal principal) throws Exception {
		logger.info("Entrando a vista para consultar numero asociadod e IMEI");
		ModelAndView modelAndView = new ModelAndView("turbooffice/phoneImei");
		ImeiPhoneModel imeiPhoneModel = new ImeiPhoneModel();
		modelAndView.addObject("imeiPhoneModel", imeiPhoneModel);
		return modelAndView;
	}
	
	@RequestMapping(value = "/turboOffice/phoneImei", method = RequestMethod.POST)
	public ModelAndView getAssociatedNumebr(ModelAndView modelAndView,
			@Valid @ModelAttribute("imeiPhoneModel") final ImeiPhoneModel imeiPhoneModel,
			BindingResult bindingResult, HttpServletRequest request, Errors errors) {
		logger.info("Obteniendo numero asociado del IME " + imeiPhoneModel.getImei());
		modelAndView = new ModelAndView("turbooffice/phoneImei");
		if (!bindingResult.hasFieldErrors()) {
			try {
				String cellphoneNumber = profileService.getCellphoneNumberByImei(imeiPhoneModel.getImei());
				if(StringUtils.isNotBlank(cellphoneNumber)) {
					imeiPhoneModel.setCellphoneNumber(cellphoneNumber);
					modelAndView.addObject("cellphoneNumber", cellphoneNumber);
				} else {
					modelAndView.addObject("failedMessage", "No se pudo validar el imei en Turbored");
				}
			} catch (TrException e) {
				imeiPhoneModel.setCellphoneNumber(null);
				modelAndView.addObject("failedMessage", e.getMessage());
			} catch (Exception e) {
				imeiPhoneModel.setCellphoneNumber(null);
				logger.error("Error al consultar el numero asociado.", e.getMessage() );
				modelAndView.addObject("failedMessage",
						"No se pudo consultar el numero asociado, intente nuevamente mas tarde");
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
	
	@RequestMapping("/turboOffice/validateIdentity")
	public ModelAndView validateIdentityView(Model model, Principal principal) throws Exception {
		logger.info("Entrando a vista para validar identidad de usuario");
		ModelAndView modelAndView = new ModelAndView("turbooffice/validateIdentity");
		ValidateIdentityModel validateIdentityModel = new ValidateIdentityModel();
		modelAndView.addObject("validateIdentityModel", validateIdentityModel);
		return modelAndView;
	}
	
//	@RequestMapping(value = "/turboOffice/validateIdentity", method = RequestMethod.POST)
//	public ModelAndView validateIdentity(ModelAndView modelAndView,
//			@Valid @ModelAttribute("imeiPhoneModel") final ValidateIdentityModel validateIdentityModel,
//			BindingResult bindingResult, HttpServletRequest request, Errors errors) {
//		logger.info("Validando numero virtual " + validateIdentityModel.getVirtualNumber());
//		modelAndView = new ModelAndView("/turbooffice/validateIdentity");
//		modelAndView.addObject("validateIdentityModel", validateIdentityModel);
//		if (!bindingResult.hasFieldErrors()) {
//			try {
//				turboOfficeService.sendSmsCodeToValidateIndentity(validateIdentityModel);
//				validateIdentityModel.setSentCode(true);
//				String confirmationMessage = "Codigo enviado a numero donde instalo la aplicación.";
//				modelAndView.addObject("confirmationMessage", confirmationMessage);
//			} catch (TrException e) {
//				modelAndView.addObject("failedMessage", e.getMessage());
//				validateIdentityModel.setSentCode(false);
//			} catch (Exception e) {
//				validateIdentityModel.setSentCode(false);
//				logger.error("Error al enviar sms para validar  identidad de usuario.", e.getMessage() );
//				modelAndView.addObject("failedMessage",
//						"No se pudo enviar sms para validar  identidad de usuario., intente nuevamente mas tarde");
//			}
//		} else {
//			modelAndView.addObject("failedMessage",
//					"La operación no pudo ser completada, verifique sus datos e intente nuevamente.");
//			for (Object object : bindingResult.getAllErrors()) {
//				if (object instanceof FieldError) {
//					FieldError fieldError = (FieldError) object;
//					logger.error(fieldError.getCode());
//				}
//				if (object instanceof ObjectError) {
//					ObjectError objectError = (ObjectError) object;
//					logger.error(objectError.getCode());
//				}
//			}
//		}
//		return modelAndView;
//	}
	
//	@RequestMapping(value = "/turboOffice/validateIdentity/validateCode", method = RequestMethod.POST)
//	public ModelAndView validateCode(ModelAndView modelAndView,
//			@Valid @ModelAttribute("imeiPhoneModel") final ValidateIdentityModel validateIdentityModel,
//			BindingResult bindingResult, HttpServletRequest request, Errors errors) {
//		logger.info("Validando codido enviado a numero dodne se instalo app " + validateIdentityModel.getValidationCode());
//		modelAndView = new ModelAndView("/turbooffice/validateIdentity");
//		modelAndView.addObject("validateIdentityModel", validateIdentityModel);
//		if (!bindingResult.hasFieldErrors()) {
//			try {
//				turboOfficeService.validateUserCode(validateIdentityModel);
//				validateIdentityModel.setSentCode(false);
//				validateIdentityModel.setValidated(true);
//				String confirmationMessage = "Codigo validado correctamente";
//				modelAndView.addObject("confirmationMessage", confirmationMessage);
//			} catch (TrException e) {
//				modelAndView.addObject("failedMessage", e.getMessage());
//				validateIdentityModel.setSentCode(false);
//				validateIdentityModel.setValidated(false);
//			} catch (Exception e) {
//				validateIdentityModel.setSentCode(false);
//				validateIdentityModel.setValidated(false);
//				logger.error("Error al validar codigo de  identidad de usuario.", e.getMessage() );
//				modelAndView.addObject("failedMessage",
//						"No se pudo enviar validar codigo  identidad de usuario., intente nuevamente mas tarde");
//			}
//		} else {
//			modelAndView.addObject("failedMessage",
//					"La operación no pudo ser completada, verifique sus datos e intente nuevamente.");
//			for (Object object : bindingResult.getAllErrors()) {
//				if (object instanceof FieldError) {
//					FieldError fieldError = (FieldError) object;
//					logger.error(fieldError.getCode());
//				}
//				if (object instanceof ObjectError) {
//					ObjectError objectError = (ObjectError) object;
//					logger.error(objectError.getCode());
//				}
//			}
//		}
//		return modelAndView;
//	}
//	
	
	
}

