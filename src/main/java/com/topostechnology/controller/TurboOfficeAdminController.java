package com.topostechnology.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.topostechnology.constant.TurboOfficeConstants;
import com.topostechnology.exception.AcrobitsException;
import com.topostechnology.exception.TrException;
import com.topostechnology.model.TurboOfficeAdminModel;
import com.topostechnology.model.TurboOfficePaymentLinkModel;
import com.topostechnology.model.TurboOfficeUserModel;
import com.topostechnology.rest.client.response.ReportResponse;
import com.topostechnology.rest.client.response.TurboOfficeCallDetail;
import com.topostechnology.service.TurboOfficeAdminService;
import com.topostechnology.utils.StringUtils;

@Controller
public class TurboOfficeAdminController {
	
	private static final Logger logger = LoggerFactory.getLogger(TurboOfficeAdminController.class);
	
	@Autowired
	private TurboOfficeAdminService turboOfficeAdminService;
	
	@Value("${callcenter.phone}")
	private String callCenterPhone;
	
	@RequestMapping("/adminTurboOffice")
	public ModelAndView turboOffice(Model model, Principal principal) {
		logger.info("Entrando a vista Turbo Office admin");
		TurboOfficeAdminModel turboOfficeAdminModel = new TurboOfficeAdminModel();
		List<TurboOfficeUserModel> turboOfficeUsers = null;
		ModelAndView modelAndView = new ModelAndView("turbooffice/admin");
		try {
			turboOfficeUsers = turboOfficeAdminService.getTurboOfficeUsers();
		} catch (Exception e) {
			modelAndView.addObject("errorMessage", "No se pudo consultar a los clientes turbo office");
			logger.error("No se ha podido consultar los cliente turbo office " + e.getMessage());
		}
		turboOfficeAdminModel.setTurboOfficeUsers(turboOfficeUsers);
		modelAndView.addObject("turboOfficeAdminModel", turboOfficeAdminModel);
		return modelAndView;
	}
	
	@RequestMapping("/adminTurboOfficeUser/{virtualNumber}")
	public ModelAndView getUserDetail(@PathVariable("virtualNumber") String virtualNumber, Principal principal) throws Exception {
		logger.info("Obteniendo detalle de usuario(numero virtual) " + virtualNumber);
		ModelAndView modelAndView = new ModelAndView("turbooffice/user");
		TurboOfficeUserModel turboOfficeUserModel = turboOfficeAdminService.getUserInfo(virtualNumber);
		modelAndView.addObject("turboOfficeUserModel", turboOfficeUserModel);
		return modelAndView;
	}
	
	@RequestMapping("/adminTurboOfficeUser/getCallsDetail/{turboOfficeUserPlanDetailId}")
	public @ResponseBody ReportResponse<TurboOfficeCallDetail> getCallsDetail(@PathVariable("turboOfficeUserPlanDetailId") Long turboOfficeUserPlanDetailId, Principal principal) throws Exception {
		logger.info("Obteniendo detalle de llamadas turboOfficeUserPlanDetailId " + turboOfficeUserPlanDetailId);
		ReportResponse<TurboOfficeCallDetail> response = new ReportResponse<>();
		List<TurboOfficeCallDetail> details = new ArrayList<TurboOfficeCallDetail>();
		try {
				details = turboOfficeAdminService.getCallsDetail(turboOfficeUserPlanDetailId);
		} catch(Exception ex) {
			logger.error("No se pudo consultar el detalle de llamadas de turboOfficeUserPlanDetailId " + turboOfficeUserPlanDetailId + " " + ex.getMessage());
		}
		response.setData(details);
	    return response;
	}
	
	@RequestMapping("/adminTurboOfficeUser/GetStatus/{virtualNumber}")
	public String getAccountStatus(@PathVariable("virtualNumber") String virtualNumber ) {
		String status = "";
		try {
			status = turboOfficeAdminService.getAcrobitsAccountStatus(virtualNumber);
		} catch (Exception e) {
			logger.error("No se pudo obtener el estatus para el numero" + virtualNumber);
		}
		return status;
	}
	
	@RequestMapping("/adminTurboOfficeUser/delete/{virtualNumber}")
	public @ResponseBody String deleteUser(@PathVariable("virtualNumber") String virtualNumber, Principal principal) throws Exception {
		logger.info("Eliminando  usuario(numero virtual) " + virtualNumber);
		String message;
		try {
			turboOfficeAdminService.deleteAccount(virtualNumber, TurboOfficeConstants.ACROBITS_ACCOUNT_DELETE_STATUS);
			message = "La cuenta ha sido dada de baja exitosamente";
		} catch (TrException e) {
			logger.error("Error al eliminar el usuario ", e);
			message = e.getMessage();
		} catch (Exception e) {
			logger.error("Error Interno al eliminar el usuario", e);
			message = "La operación no pudo ser realizada, intente nuevamente más tarde";
		}
		return message;
	}
	
	@RequestMapping("/adminTurboOfficeUser/generatePaymentLink/{virtualNumber}")
	public @ResponseBody String generatePaymentLink(@PathVariable("virtualNumber") String virtualNumber, Principal principal) throws Exception {
		logger.info("Generar link de pago para  usuario(numero virtual) " + virtualNumber);
		String paymentLink = null;
		try {
			paymentLink = turboOfficeAdminService.generatePaymentLink(virtualNumber);
		} catch (TrException e) {
			logger.error("Error al genrear link de pago para el usuario ", e);
			throw e;
		} catch (Exception e) {
			logger.error("Error Interno al genrear link de pago para el usuario " + virtualNumber +" " + e);
			throw e;
		} return paymentLink;
	}
	
	@RequestMapping("/adminTurboOffice/unlockAcrobitsUser/{virtualNumber}")
	public @ResponseBody String unlockAcrobitsUser(@PathVariable("virtualNumber") String virtualNumber, Principal principal) throws Exception {
		logger.info("Desbloquenado de acrobits al  usuario(numero virtual) " + virtualNumber);
		String message = "";
		try {
			turboOfficeAdminService.unlockAcrobitsAccount(virtualNumber);
			message = "Usuario desbloqueado exitosamente en acrobits";
		}catch(AcrobitsException ae) {
			logger.error("No se pudo realizar el desbloqueo en  acrobits   " + ae);
			message = "No se pudo realizar el desbloqueo en  acrobits";
		} catch (TrException e) {
			logger.error("Error al desbloquear usuario en acrobits " + virtualNumber + e);
			message = e.getMessage();
		} catch (Exception e) {
			logger.error("Error Interno al desbloquear en acrobits al usuario "+ virtualNumber + e);
			message = "No se ha podido desbloquear usuario en acrobits.";
		} 
		return message;
	}
	
	@RequestMapping("/adminTurboOffice/getAcrobitsLockedStatus/{virtualNumber}")
	public @ResponseBody Boolean getAcrobitsLockedStatus(@PathVariable("virtualNumber") String virtualNumber, Principal principal) throws Exception {
		logger.info("Desbloquenado de acrobits al  usuario(numero virtual) " + virtualNumber);
		Boolean acrobitsLockedStatus = null;
		try {
			acrobitsLockedStatus = turboOfficeAdminService.getAcrobitsLockedStatus(virtualNumber);
		} catch (Exception e) {
			acrobitsLockedStatus = null;
		} 
		return acrobitsLockedStatus;
	}
	
	@RequestMapping(value = "/adminTurboOffice/sendValidationCode/{virtualNumber}")
	public @ResponseBody Boolean validateIdentity(@PathVariable("virtualNumber") String virtualNumber,
			Principal principal) {
		logger.info("Validando numero virtual " + virtualNumber);
		try {
			turboOfficeAdminService.sendSmsCodeToValidateIndentity(virtualNumber);
			return true;
		} catch (Exception e) {
			logger.error(virtualNumber + " " + e.getMessage());
			return false;
		}
	}

	@RequestMapping(value = "/adminTurboOffice/validateCode/{virtualNumber}/{validationCode}")
	public @ResponseBody Boolean validateCode(@PathVariable("virtualNumber") String virtualNumber,
			@PathVariable("validationCode") String validationCode, Principal principal) {
		logger.info("Validando codido enviado a numero donde se instalo app (code) " + virtualNumber + "-"
				+ validationCode);
		try {
			if (StringUtils.isNotBlank(validationCode)) {
				turboOfficeAdminService.validateUserCode(virtualNumber, validationCode);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			logger.error(virtualNumber + " " + e.getMessage());
			return false;
		}
	}

	@RequestMapping("/adminTurboOffice/paymentLink")
	public ModelAndView getLinkPago(Model model, Principal principal) throws Exception {
		logger.info("Obteniendo link pago pantalla");
		TurboOfficePaymentLinkModel turboOfficePaymentLinkModel = new TurboOfficePaymentLinkModel();
		ModelAndView modelAndView = new ModelAndView("turbooffice/linkPago");
		modelAndView.addObject("turboOfficePaymentForm", turboOfficePaymentLinkModel);
		return modelAndView;
	}

@RequestMapping(value = "/adminTurboOffice/paymentLink", method = RequestMethod.POST)
	public ModelAndView validate(ModelAndView modelAndView,
								 @Valid @ModelAttribute("turboOfficePaymentForm") final TurboOfficePaymentLinkModel turboOfficePaymentLinkModel,
								 BindingResult bindingResult, HttpServletRequest requeCompatibilityModelst, Errors errors) {
		logger.info("Validando datos link de pago " + turboOfficePaymentLinkModel.getLink() + " - "+turboOfficePaymentLinkModel.getMsisdn());
		modelAndView = new ModelAndView("turbooffice/linkPago");
		bindingResult = turboOfficeAdminService.validatePaymentLink(turboOfficePaymentLinkModel, bindingResult);
		String message ="";
		if (!bindingResult.hasFieldErrors()) {
			try {
				//compatibilityModelNew = compatibilityService.checkImei(compatibilityModel.getImei());
				//String message = compatibilityModelNew.getMessage();
				// logger.info("Mensaje a devolver " + message);
				message = turboOfficeAdminService.linkPago(turboOfficePaymentLinkModel);

					modelAndView.addObject("confirmationMessage", message);

			} catch (Exception e) {
				logger.error("Error on validate compatibility.", e.getMessage());
				bindingResult.reject("failedMessage",
						"La operación no pudo ser realizada, intente nuevamente más tarde");
				modelAndView.addObject("failedMessage", e.getMessage());
			}
		}

		return modelAndView;
	}


@RequestMapping("/adminTurboOffice/adminUsers")
	public ModelAndView turboOfficeUser(Model model, Principal principal) {
		logger.info("Entrando a vista Turbo Office admin");
		TurboOfficeAdminModel turboOfficeAdminModel = new TurboOfficeAdminModel();
		List<TurboOfficeUserModel> turboOfficeUsers = null;
		ModelAndView modelAndView = new ModelAndView("turbooffice/adminUser");
		try {
			turboOfficeUsers = turboOfficeAdminService.getTurboOfficeUsers();
		} catch (Exception e) {
			modelAndView.addObject("errorMessage", "No se pudo consultar a los clientes turbo office");
			logger.error("No se ha podido consultar los cliente turbo office " + e.getMessage());
		}
		turboOfficeAdminModel.setTurboOfficeUsers(turboOfficeUsers);
		modelAndView.addObject("turboOfficeAdminModel", turboOfficeAdminModel);
		return modelAndView;
	}
	
@RequestMapping("/adminTurboOffice/unlinkVirtualNumber/{virtualNumber}/{associatedNumber}")
public @ResponseBody String desvincularNumeroVirtual(@PathVariable("virtualNumber") String virtualNumber, @PathVariable("associatedNumber") String associatedNumber, Principal principal) throws Exception {
	logger.info("Desvincular numero virtual " + virtualNumber);
	String message = "";
	try {
		turboOfficeAdminService.desvincularNumeroVirtual(virtualNumber, associatedNumber);
		message = "Usuario desbloqueado exitosamente en acrobits";
	}catch(AcrobitsException ae) {
		logger.error("No se pudo desvincular    " + ae);
		message = "No se pudo desvincular ";
	} catch (TrException e) {
		logger.error("Error al desvincular numero " + virtualNumber + e);
		message = e.getMessage();
	} catch (Exception e) {
		logger.error("Error Interno al desvincular"+ virtualNumber + e);
		message = "No se ha podido desvincular el numero virtual: "+virtualNumber;
	}
	return message;
}
}
