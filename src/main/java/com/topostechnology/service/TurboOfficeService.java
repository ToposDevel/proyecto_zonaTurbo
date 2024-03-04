package com.topostechnology.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.topostechnology.constant.ResponseCodeConstants;
import com.topostechnology.constant.TurboOfficeConstants;
import com.topostechnology.constant.UserConstants;
import com.topostechnology.exception.TrException;
import com.topostechnology.model.TurboOfficeActivationModel;
import com.topostechnology.model.TurboOfficeRechargeModel;
import com.topostechnology.model.ValidateIdentityModel;
import com.topostechnology.rest.client.PaymentLinkClient;
import com.topostechnology.rest.client.TurboApiClient;
import com.topostechnology.rest.client.request.PaymentLinkRequest;
import com.topostechnology.rest.client.request.PaymentLinkResponse;
import com.topostechnology.rest.client.request.PyLCustomer;
import com.topostechnology.rest.client.request.PyLProduct;
import com.topostechnology.rest.client.request.PyLPurshaseOrder;
import com.topostechnology.rest.client.request.TurboOfficeExtraRechargeRequest;
import com.topostechnology.rest.client.request.TurboOfficePlanResponse;
import com.topostechnology.rest.client.request.TurboOfficeRegisterRequest;
import com.topostechnology.rest.client.request.UserValidationCodeRequest;
import com.topostechnology.rest.client.response.TurboOfficePlanDto;
import com.topostechnology.rest.client.response.TurboOfficeUserData;
import com.topostechnology.rest.client.response.TurboOfficeUserInfoResponse;
import com.topostechnology.rest.client.response.TurboOfficeUserPlanDetailResponse;
import com.topostechnology.utils.StringUtils;
import com.topostechnology.validation.GeneralValidator;

@Service
public class TurboOfficeService {
	
	private static final Logger logger = LoggerFactory.getLogger(TurboOfficeService.class);
	
	@Autowired
	private PaymentLinkClient paymentLinkClient;
	
	@Value("${turbo.office.payment.link.expiration.days}")
	private int paymentLinkExpirationDays;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private ProfileService profileService;
	
	@Autowired
	private TurboApiClient turboApiClient;
	
	@Value("${callcenter.phone}")
	private String callCenterPhone;
	
	public TurboOfficeActivationModel getTurboOficePlansInfo() throws Exception {
		TurboOfficeActivationModel turboOfficeActivationModel = new TurboOfficeActivationModel();
		turboOfficeActivationModel.setCompany(TurboOfficeConstants.TURBO_OFFICE_DEFAULT_COMPANY);
		TurboOfficePlanResponse turboOfficePlanResponse;
			turboOfficePlanResponse = turboApiClient.getTurboOfficeActivePlans();
			if(turboOfficePlanResponse.getCode() == ResponseCodeConstants.SUCCES_CODE) {
				List<TurboOfficePlanDto>  turboOfficePlans = turboOfficePlanResponse .getTurboOfficePlans();
				TurboOfficePlanDto turboOfficePlanDefault = turboOfficePlans.get(0);  // TODO default
				turboOfficeActivationModel.setTurboOfficePlans(turboOfficePlans);
				turboOfficeActivationModel.setPrice(turboOfficePlanDefault.getPrice()); // TODO default
				turboOfficeActivationModel.setTurboOfficePlanName(turboOfficePlanDefault.getName()); // TODO default
			} else {
				throw new TrException("Error al consultar plan disponible");
			}
		return turboOfficeActivationModel;
	}
	
	public String saveTurboUserAndGetPaymentLink(TurboOfficeActivationModel turboOfficeActivationModel) throws Exception {
		String paymentLink = null;
		profileService.isTurbored(turboOfficeActivationModel.getCellphoneNumber());
		this.isOfferExpired(turboOfficeActivationModel.getCellphoneNumber());
		PaymentLinkRequest paymentLinkRequest = this.createPaymentLinkRequest(turboOfficeActivationModel.getFullName(), turboOfficeActivationModel.getCellphoneNumber(), turboOfficeActivationModel.getEmail(),
				turboOfficeActivationModel.getTurboOfficePlanName(), turboOfficeActivationModel.getPrice(), paymentLinkExpirationDays, TurboOfficeConstants.TURBO_OFFICE_ACTIVATION);
		PaymentLinkResponse paymentLinkResponse = paymentLinkClient.getPaymentLink(paymentLinkRequest);
		logger.info("Response code" + paymentLinkResponse.getCodigo());
		if(paymentLinkResponse.getCodigo().equals("0")) { // Codigo que usa este ws para caso de exito 
			paymentLink = paymentLinkResponse.getUrl();
		turboOfficeActivationModel.setPaymentLink(paymentLink);
		this.saveTurboOfficeUser(turboOfficeActivationModel);
		this.sendPaymentLinkNotification(turboOfficeActivationModel.getEmail(), turboOfficeActivationModel.getCellphoneNumber(), turboOfficeActivationModel.getFullName(), paymentLink, "Activacion");
		this.cleanTurboOfficeForm(turboOfficeActivationModel);
		} else {
			throw new TrException("El link de pago no pudo ser generado, comunicate al centro de atencion " + callCenterPhone);
		}
		return paymentLink;
	}
	
	private void isOfferExpired(String cellphoneNumber) throws TrException {
		Boolean isExpired = profileService.isOfferExpired(cellphoneNumber);
		if(isExpired) {
			logger.error("Para poder contratar turbo office su oferta debe estar vigente " + callCenterPhone);
			throw new TrException("Para poder contratar turbo office tu oferta debe estar vigente ");
		}
	}
	
	private PaymentLinkRequest createPaymentLinkRequest(String fullName, String cellphoneNumber, String email,
			String turboOfficePlanName, Integer price, int paymentLinkExpirationDays, String origin) {
		PaymentLinkRequest paymentLinkRequest = new PaymentLinkRequest();
		paymentLinkRequest.setMsisdn(cellphoneNumber);
		paymentLinkRequest.setDiasExpiracion(paymentLinkExpirationDays);
		paymentLinkRequest.setOrigen(origin);
		PyLPurshaseOrder pyLPurshaseOrder = this.createPyLPurshaseOrder(fullName, cellphoneNumber, email,
				turboOfficePlanName, price);
		paymentLinkRequest.setOrdenCompra(pyLPurshaseOrder);
		return paymentLinkRequest;
	}
	
	private PyLPurshaseOrder createPyLPurshaseOrder(String fullName, String cellphoneNumber, String email, String turboOfficePlanName, Integer price) {
		PyLPurshaseOrder pyLPurshaseOrder = new PyLPurshaseOrder();
		PyLCustomer customer = new PyLCustomer();
		customer.setCorreo(email);
		customer.setNombre(fullName);
		customer.setTelefono(cellphoneNumber);
		List<PyLProduct> productList = new ArrayList<>();
		PyLProduct pyLProduct = new PyLProduct();
		pyLProduct.setCantidad(TurboOfficeConstants.A_UNIT);
		pyLProduct.setNombre(turboOfficePlanName);
		pyLProduct.setPrecioUnitario(price);
		productList.add(pyLProduct);
		pyLPurshaseOrder.setCliente(customer);
		pyLPurshaseOrder.setProductos(productList);
		return pyLPurshaseOrder;
	}
	
	private void saveTurboOfficeUser(TurboOfficeActivationModel turboOfficeActivationModel) throws Exception {
		TurboOfficeRegisterRequest tOfficeRegisterRequest = new TurboOfficeRegisterRequest();
		tOfficeRegisterRequest.setCellphoneNumber(turboOfficeActivationModel.getCellphoneNumber());
		tOfficeRegisterRequest.setCompany(turboOfficeActivationModel.getCompany());
		tOfficeRegisterRequest.setEmail(turboOfficeActivationModel.getEmail());
		tOfficeRegisterRequest.setFullName(turboOfficeActivationModel.getFullName());
		tOfficeRegisterRequest.setImei(turboOfficeActivationModel.getImei());
		tOfficeRegisterRequest.setPaymentLink(turboOfficeActivationModel.getPaymentLink());
		tOfficeRegisterRequest.setPrice(turboOfficeActivationModel.getPrice());
		tOfficeRegisterRequest.setTurboOfficePlanName(turboOfficeActivationModel.getTurboOfficePlanName());
		turboApiClient.saveTurboOfficeUser(tOfficeRegisterRequest);
	}
	
	public BindingResult validateTurboOfficeForm(TurboOfficeActivationModel turboOfficeActivationModel, BindingResult bindingResult) {
		if (StringUtils.isBlank(turboOfficeActivationModel.getFullName())) {
			bindingResult.rejectValue("fullName", "mandatory.field");
		}
		if (!GeneralValidator.validatePattern(UserConstants.REGEX_EMAIL, turboOfficeActivationModel.getEmail())) {
			bindingResult.rejectValue("email", "email.incorrect");
		}
		if(!GeneralValidator.validatePattern(UserConstants.REGEX_IMEI_NUMBER, turboOfficeActivationModel.getImei())) {
				bindingResult.rejectValue("imei", "imei.number.fifteen");
			}
		if (!GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER,
				turboOfficeActivationModel.getCellphoneNumber())) {
				bindingResult.rejectValue("cellphoneNumber", "cellphone.number.then");
		}
		if (!GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER,
				turboOfficeActivationModel.getCellphoneNumberConfirmation())) {
			bindingResult.rejectValue("cellphoneNumberConfirmation", "cellphone.number.then");
		}
		if (!turboOfficeActivationModel.getCellphoneNumber().equals(turboOfficeActivationModel.getCellphoneNumberConfirmation())) {
			bindingResult.rejectValue("cellphoneNumber", "cellphone.not.equal.message");
		}
		
		
		return bindingResult;
	}
	
	public BindingResult validateExtraRechargeForm(TurboOfficeRechargeModel turboOfficeRechargeModel, BindingResult bindingResult) {
		if (!GeneralValidator.validatePattern(UserConstants.REGEX_EMAIL, turboOfficeRechargeModel.getEmail())) {
			bindingResult.rejectValue("email", "email.incorrect");
		}
		if (!GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER, turboOfficeRechargeModel.getVirtualNumber())) {
				bindingResult.rejectValue("virtualNumber", "cellphone.number.then");
		}
		return bindingResult;
	}
	
	public void cleanTurboOfficeForm(TurboOfficeActivationModel turboOfficeActivationModel) {
		turboOfficeActivationModel.setCellphoneNumber("");
		turboOfficeActivationModel.setEmail("");
		turboOfficeActivationModel.setFullName("");
		turboOfficeActivationModel.setImei("");
		turboOfficeActivationModel.setPrice(null);
	}
	
	public TurboOfficePlanDto getTurboOficeRechargeOptions() throws Exception {
		TurboOfficePlanDto turboOfficePlanDto = new TurboOfficePlanDto();
		turboOfficePlanDto.setMinutes(100);
//		turboOfficePlanDto.setName("Recarga 100 minutos - $ 50");
		turboOfficePlanDto.setName("Recarga 100 minutos");
		turboOfficePlanDto.setPrice(50);
		return turboOfficePlanDto;
	}
	
	public String getTurboOficeRechargeOption() throws Exception {
		return "TO0350R-100M";
	}

	public String saveExtraRechargeAndGetPaymentLink(TurboOfficeRechargeModel turboOfficeRechargeModel)  throws Exception {
		String paymentLink = null;
		TurboOfficeUserPlanDetailResponse turboOfficeUserPlanDetailResponse = turboApiClient.getTurboOfficeUserPlanDetail(turboOfficeRechargeModel.getVirtualNumber()); 
		String fullUserName = null;
		if(turboOfficeUserPlanDetailResponse.getCode()  == ResponseCodeConstants.SUCCES_CODE) { // verifica  si esta activo el pago del periodo actual
			fullUserName = turboOfficeUserPlanDetailResponse.getFullUserName();
			PaymentLinkRequest paymentLinkRequest = this.createPaymentLinkRequest(fullUserName, turboOfficeRechargeModel.getVirtualNumber(), turboOfficeRechargeModel.getEmail(),
					turboOfficeRechargeModel.getTurboOfficePlanName(), turboOfficeRechargeModel.getPrice(), paymentLinkExpirationDays, TurboOfficeConstants.TURBO_OFFICE_EXTRA_RECHARGE);
			
			PaymentLinkResponse paymentLinkResponse = paymentLinkClient.getPaymentLink(paymentLinkRequest);
			logger.info("Response code " + paymentLinkResponse.getCodigo());
			if(paymentLinkResponse.getCodigo().equals("0")) { // Codigo que usa este ws para caso de exito 
				turboOfficeRechargeModel.setPaymentLink(paymentLinkResponse.getUrl());
			this.saveExtraRechargeRequest(turboOfficeRechargeModel, turboOfficeUserPlanDetailResponse.getTOfficeUserPlanDetailId());
			paymentLink = paymentLinkResponse.getUrl();
			this.sendPaymentLinkNotification(turboOfficeRechargeModel.getEmail(), turboOfficeRechargeModel.getVirtualNumber(), fullUserName, paymentLink, "Recarga adicional");
//			this.cleanTurboOfficeForm(turboOfficeActivationModel);
			
			} else {
				throw new TrException("El link de pago no pudo ser generado, comunicate al call center");
			}
		} else {  //si NO esta activo el pago del periodo actual
			throw new TrException("No se permite recarga adicional para este usuario, es necesario que el servicio este vigente.");
		}
		return paymentLink;
	}
	
	private void saveExtraRechargeRequest(TurboOfficeRechargeModel turboOfficeRechargeModel, Long tOfficeUserPlanDetailId) throws Exception {
		TurboOfficeExtraRechargeRequest turboOfficeExtraRechargeRequest = new TurboOfficeExtraRechargeRequest();
		turboOfficeExtraRechargeRequest.setCompany(TurboOfficeConstants.TURBO_OFFICE_DEFAULT_COMPANY);
		turboOfficeExtraRechargeRequest.setEmail(turboOfficeRechargeModel.getEmail());
		turboOfficeExtraRechargeRequest.setPaymentLink(turboOfficeRechargeModel.getPaymentLink());
		turboOfficeExtraRechargeRequest.setTOfficeUserPlanDetailId(tOfficeUserPlanDetailId);
		turboOfficeExtraRechargeRequest.setTurboOfficePlanName(turboOfficeRechargeModel.getTurboOfficePlanName());
		turboOfficeExtraRechargeRequest.setVirtualNumber(turboOfficeRechargeModel.getVirtualNumber());
		turboApiClient.saveExtraRechargeRequest(turboOfficeExtraRechargeRequest);
	}
	
	public void processPeriodPayments() throws Exception {
		turboApiClient.processPeriodPayments();
	}
	
	
	public void sendPaymentLinkNotification(String email, String cellphoneNumber, String customerName, String paymentLink, String operation) {
		if(StringUtils.isNotBlank(email)) {
			try {
				String message = "<p>Dar clic en el siguiente </>"
						+ "<a href='"+paymentLink + "'<b>LINK DE PAGO </b></a>"
						+ "<p>para realizar tu pago del servicio turbo office.</p><br>"
						+ "<p>*Si ya realizaste el pago, favor de hacer caso omiso a este mensaje.</p>";
						
				
				emailService.sendSimpleAutamaticFormatNotification(customerName,  cellphoneNumber, 
						email, "Turbo office " + operation, message, null);
			} catch (Exception e) {
				logger.info("La notificación con el link de pago para " +cellphoneNumber +" no pudo ser enviada por correo a " + email
						+" Error msg " + e.getMessage());
			}
		}
	}
	
	
//	public void sendSmsCodeToValidateIndentity(ValidateIdentityModel validateIdentityModel) throws Exception {
//		if(validateIdentityModel.getVirtualNumber().equals(validateIdentityModel.getVirtualNumberConfirmation())) {
//			TurboOfficeUserInfoResponse turboOfficeUserInfoResponse = this.turboApiClient.getUserInfo(validateIdentityModel.getVirtualNumber());
//			if(turboOfficeUserInfoResponse != null) {
//				TurboOfficeUserData turboOfficeUserData = turboOfficeUserInfoResponse.getTurboOfficeUserData();
//				String mobileInstallNumber = turboOfficeUserData.getMobileInstallNumber();
//				UserValidationCodeRequest userValidationCodeRequest = new UserValidationCodeRequest();
//				userValidationCodeRequest.setCellphoneNumber(mobileInstallNumber);
//				userValidationCodeRequest.setOrigin(TurboOfficeConstants.TURBO_OFFICE_IDENTITY);
//				turboApiClient.sendUserCodeBySmsWs(userValidationCodeRequest);
//				logger.info("Codigo enviado");
//			} else {
//				throw new TrException("Numero no encontrado.");
//			}
//		} else {
//			throw new TrException("Los numeros ingresados no coinciden");
//		}
//		
//	}
//	
//	public void validateUserCode(ValidateIdentityModel validateIdentityModel) throws Exception {
//				UserValidationCodeRequest userValidationCodeRequest = new UserValidationCodeRequest();
//				userValidationCodeRequest.setCellphoneNumber(validateIdentityModel.getMobileInstallNumber());
//				userValidationCodeRequest.setOrigin(TurboOfficeConstants.TURBO_OFFICE_IDENTITY);
//				turboApiClient.validateUserCode(userValidationCodeRequest);
//				logger.info("Codigo validado");
//				// TODO AGREGAR CAMPO PARA REGISTRA CAMBIO
//	}
	
	
	
	
}
