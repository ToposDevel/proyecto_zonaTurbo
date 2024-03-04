package com.topostechnology.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.topostechnology.constant.ResponseCodeConstants;
import com.topostechnology.constant.TurboOfficeConstants;
import com.topostechnology.constant.UserConstants;
import com.topostechnology.controller.TurboOfficeAdminController;
import com.topostechnology.exception.AcrobitsException;
import com.topostechnology.exception.TrException;
import com.topostechnology.exception.TurboApiException;
import com.topostechnology.model.TurboOfficePaymentLinkModel;
import com.topostechnology.model.TurboOfficeUserModel;
import com.topostechnology.model.TurboOfficeUserPlanDetail;
import com.topostechnology.model.ValidateIdentityModel;
import com.topostechnology.rest.client.AcrobitUserClient;
import com.topostechnology.rest.client.TurboApiClient;
import com.topostechnology.rest.client.request.AcrobitsChangeAcountStatusRequest;
import com.topostechnology.rest.client.request.AcrobitsCheckAcountStatusRequest;
import com.topostechnology.rest.client.request.ManagementServiceRequest;
import com.topostechnology.rest.client.request.PaymentLinkNotificationRequest;
import com.topostechnology.rest.client.request.UserValidationCodeRequest;
import com.topostechnology.rest.client.response.GeneralResponse;
import com.topostechnology.rest.client.response.PaymentLinkResponse;
import com.topostechnology.rest.client.response.TurboOfficeCallDetail;
import com.topostechnology.rest.client.response.TurboOfficeCallDetailResponse;
import com.topostechnology.rest.client.response.TurboOfficeUserData;
import com.topostechnology.rest.client.response.TurboOfficeUserInfoResponse;
import com.topostechnology.rest.client.response.TurboOfficeUserPlanDetailData;
import com.topostechnology.rest.client.response.TurboOfficeUsersResponse;
import com.topostechnology.utils.StringUtils;
import com.topostechnology.validation.GeneralValidator;

import java.util.Date;
import java.util.List;

import com.topostechnology.constant.UserConstants;
import com.topostechnology.domain.TurboOfficeUser;
import com.topostechnology.domain.VirtualNumber;
import com.topostechnology.model.*;
import com.topostechnology.repository.TurboOfficeUserRepository;
import com.topostechnology.repository.VirtualNumberRepository;
import com.topostechnology.rest.client.GategayApiClient;

@Service
public class TurboOfficeAdminService {
  private static final Logger logger = LoggerFactory.getLogger(TurboOfficeAdminController.class);
  
  @Autowired
  private AcrobitUserClient acrobitUserClient;
  
  @Autowired
  private TurboApiClient turboApiClient;
  
  @Autowired
	private EmailService emailService;
  
  @Autowired
  private GategayApiClient gategayApiClient;

@Autowired
private TurboOfficeUserRepository toOfficeUserRepository;

  @Autowired
  private VirtualNumberRepository virtualNumberRepository;
  
  public List<TurboOfficeUserModel> getTurboOfficeUsers() throws Exception {
    List<TurboOfficeUserModel> turboOfficeUsers = new ArrayList<>();
    TurboOfficeUsersResponse turboOfficeUsersResponse = this.turboApiClient.getTurboOfficeUsers();
    List<TurboOfficeUserData> turboOfficeUserDataList = turboOfficeUsersResponse.getTurboOfficeUserDataList();
    for (TurboOfficeUserData turboOfficeUserData : turboOfficeUserDataList) {
      TurboOfficeUserModel turboOfficeUserModel = new TurboOfficeUserModel();
      turboOfficeUserModel.setAssociatedNumber(turboOfficeUserData.getAssociatedNumber());
      turboOfficeUserModel.setVirtualNumber(turboOfficeUserData.getVirtualNumber());
      turboOfficeUserModel.setFullName(turboOfficeUserData.getFullUserName());
      turboOfficeUserModel.setTurboOfficeStatus(turboOfficeUserData.getTurboOfficeStatus());
      turboOfficeUserModel.setTurboOfficePlanName(turboOfficeUserData.getPlanName());
      turboOfficeUserModel.setEmail(turboOfficeUserData.getEmail());
      turboOfficeUserModel.setCreatedAt(turboOfficeUserData.getCreatedAt());
      turboOfficeUserModel.setLastPaymentAt(turboOfficeUserData.getLastPaymentAt());
      turboOfficeUserModel.setExpiredAt(turboOfficeUserData.getLastExpireAt());
      turboOfficeUsers.add(turboOfficeUserModel);
    } 
    return turboOfficeUsers;
  }
  
  public TurboOfficeUserModel getUserInfo(String virtualNumber) throws Exception {
    TurboOfficeUserModel turboOfficeUserModel = null;
    if(virtualNumber != null) {
    	TurboOfficeUserInfoResponse turboOfficeUserInfoResponse = this.turboApiClient.getUserInfo(virtualNumber);
        TurboOfficeUserData turboOfficeUserData = turboOfficeUserInfoResponse.getTurboOfficeUserData();
        if (turboOfficeUserData != null) {
          turboOfficeUserModel = new TurboOfficeUserModel();
          turboOfficeUserModel.setAssociatedNumber(turboOfficeUserData.getAssociatedNumber());
          turboOfficeUserModel.setVirtualNumber(turboOfficeUserData.getVirtualNumber());
          turboOfficeUserModel.setFullName(turboOfficeUserData.getFullUserName());
          turboOfficeUserModel.setTurboOfficeStatus(turboOfficeUserData.getTurboOfficeStatus());
          turboOfficeUserModel.setTurboOfficePlanName(turboOfficeUserData.getPlanName());
          turboOfficeUserModel.setEmail(turboOfficeUserData.getEmail());
          turboOfficeUserModel.setCreatedAt(turboOfficeUserData.getCreatedAt());
          turboOfficeUserModel.setLastPaymentAt(turboOfficeUserData.getLastPaymentAt());
          turboOfficeUserModel.setExpiredAt(turboOfficeUserData.getLastExpireAt());
          turboOfficeUserModel.setTotalPeridoMinutes(turboOfficeUserData.getTotalPeridoMinutes());
          turboOfficeUserModel.setTotalPlanMinutes(turboOfficeUserData.getTotalPlanMinutes());
          turboOfficeUserModel.setTotalExtraRechargeTotalMinutes(turboOfficeUserData.getTotalExtraRechargeTotalMinutes());
          turboOfficeUserModel.setTotalConsumedMinutes(turboOfficeUserData.getTotalConsumedMinutes());
          turboOfficeUserModel.setTotalAvailableMinutes(turboOfficeUserData.getTotalAvailableMinutes());
          String acrobitsStatus = getAcrobitsAccountStatus(virtualNumber);
          turboOfficeUserModel.setAcrobitsStatus((acrobitsStatus != null) ? acrobitsStatus : "NO DISPONIBLE");
          turboOfficeUserModel.setAcrobitsLockedStatus(this.getAcrobitsLockedStatus(virtualNumber));
          List<TurboOfficeUserPlanDetailData> planDetailsData = turboOfficeUserData.getPlanDetails();
          List<TurboOfficeUserPlanDetail> planDetails = new ArrayList<>();
          for (TurboOfficeUserPlanDetailData topdd : planDetailsData) {
            TurboOfficeUserPlanDetail turboOfficeUserPlanDetail = new TurboOfficeUserPlanDetail();
            turboOfficeUserPlanDetail.setActivatedAt(topdd.getActivatedAt());
            turboOfficeUserPlanDetail.setExpireAt(topdd.getExpireAt());
            turboOfficeUserPlanDetail.setTurboOfficeUserPlanDetailId(topdd.getTurboOfficeUserPlanDetailId());
            turboOfficeUserPlanDetail.setPeriod(topdd.getPeriod());
            planDetails.add(turboOfficeUserPlanDetail);
          } 
          turboOfficeUserModel.setPlanDetails(planDetails);
        }
    }
    return turboOfficeUserModel;
  }
  
  public String getAcrobitsAccountStatus(String userName) {
	  AcrobitsCheckAcountStatusRequest checkAcountStatusRequest = new AcrobitsCheckAcountStatusRequest();
    checkAcountStatusRequest.setCloud_username(userName);
    String status = null;
    try {
      status = this.acrobitUserClient.checkAccountStatusWs(checkAcountStatusRequest);
    } catch (Exception e) {
      logger.error("No se pudo consultar el estatus del numero virtual en acrobits para " + userName + " " + e.getMessage());
    } 
    return status;
  }
  
  public List<TurboOfficeCallDetail> getCallsDetail(Long turboOfficeUserPlanDetailId) throws Exception {
    TurboOfficeCallDetailResponse turboOfficeCallDetailResponse = this.turboApiClient.getCallDetailsWs(turboOfficeUserPlanDetailId);
    List<TurboOfficeCallDetail> details = turboOfficeCallDetailResponse.getCallDetails();
    return details;
  }
  
  public void deleteAccount(String virtualNumber, String newStatus) throws Exception {
	  AcrobitsChangeAcountStatusRequest changeAcountStatusRequest = new AcrobitsChangeAcountStatusRequest();
    changeAcountStatusRequest.setCloud_username(virtualNumber);
    changeAcountStatusRequest.setNew_status(newStatus);
    try {
      this.acrobitUserClient.changeAccountStatus(changeAcountStatusRequest);
      GeneralResponse generalResponse = this.turboApiClient.deleteUser(virtualNumber);
      if (generalResponse != null) {
        if (generalResponse.getCode() == 0) {
          logger.error("El usuario no pudo ser eliminado en turbo office " + virtualNumber + " " + generalResponse.getMessage());
          throw new TrException("Se ha generado un error al borrar el registro en Turbo office, intentalo mas tarde. ");
        } 
        logger.info("Se completo el proceso de eliminacion en acrobits y el registro en turbo office para " + virtualNumber + " " + generalResponse.getMessage());
      } else {
        logger.error("Se ha generado un error al borrar el registro en Turbo office generalResponse " + generalResponse);
        throw new TrException("Se ha generado un error al borrar el registro en Turbo office, intentalo mas tarde. ");
      } 
    } catch (AcrobitsException e) {
      logger.info("Se ha generado un error al borrar el usuario en acrobits app. " + e.getMessage());
      throw new AcrobitsException("Se ha generado un error al borrar el usuario en acrobits app. ");
    } catch (TurboApiException e) {
      logger.info("Se ha generado un error al borrar el registro en Turbo office en turboAppi " + e.getMessage());
      throw new TrException("Se ha generado un error al borrar el registro en Turbo office, intentalo mas tarde ");
    } catch (Exception e) {
      logger.info("Se ha generado un error al borrar el registro en Turbo office" + e.getMessage());
      throw new TrException("Se ha generado un error al borrar el registro en Turbo office, intentalo mas tarde. ");
    } 
  }
  
  public String generatePaymentLink(String virtualNumber) throws Exception {
    String paymentLink = null;
    try {
      PaymentLinkResponse paymentLinkResponse = this.turboApiClient.generatePaymentLink(virtualNumber);
      if (paymentLinkResponse.getCode() == 1) {
        paymentLink = paymentLinkResponse.getPaymentLink();
      } else {
        throw new TrException(paymentLinkResponse.getMessage());
      } 
    } catch (Exception e) {
      logger.info("Se ha generado un error interno al generar el link de pago para  " + virtualNumber + " " + e.getMessage());
      throw new TrException("Se ha generado un error al borrar el registro en Turbo office, intentalo mas tarde. ");
    } 
    return paymentLink;
  }
  
//	public String getAcrobitsLockedStatus(String virtualNumber) {
//		String locked = null;
//		String requestedFields = "{\"requested_fields\" : \"cloud_username, account_id, locked\", "
//				+ "\"cloud_username\" : \"" + virtualNumber + "\"}";
//		String jsonResponse;
//		try {
//			jsonResponse = acrobitUserClient.getUserAccount(virtualNumber, requestedFields);
//			JSONObject objectJson = new JSONObject(jsonResponse);
//			Boolean lockedResp = objectJson.getBoolean("locked");
//			if(lockedResp != null) {
//				locked = lockedResp.toString();
//			}
//		} catch (AcrobitsException e) {
//			logger.error("Error  " + e.getMessage());
//		} catch (Exception e){
//			logger.error("No se pudo obtener el status locked de acrobits App   " + e);
//		}
//		return locked;
//	}
  
	public Boolean getAcrobitsLockedStatus(String virtualNumber) {
		Boolean locked = null;
		String requestedFields = "{\"requested_fields\" : \"cloud_username, account_id, locked\", "
				+ "\"cloud_username\" : \"" + virtualNumber + "\"}";
		String jsonResponse;
		try {
			jsonResponse = acrobitUserClient.getUserAccount(virtualNumber, requestedFields);
			JSONObject objectJson = new JSONObject(jsonResponse);
			locked = objectJson.getBoolean("locked");
		} catch (AcrobitsException e) {
			logger.error("Error  " + e.getMessage());
		} catch (Exception e){
			logger.error("No se pudo obtener el status locked de acrobits App   " + e);
		}
		return locked;
	}
  
	public void unlockAcrobitsAccount(String virtualNumber) throws Exception {
		Long accountId = this.getAcrobitsAccountId(virtualNumber);
		TurboOfficeUserInfoResponse turboOfficeUserInfoResponse = this.turboApiClient.getUserInfo(virtualNumber);
		String newPassword =this.generateNewPassword();
		logger.info("newPassword: " + newPassword);
		String email = turboOfficeUserInfoResponse.getTurboOfficeUserData().getEmail();
		String requestedFields = "{\"updated_data\" : { \"locked\" : \"false\", "
				+ "\"cloud_password\" : \"" + newPassword +"\"," 
				+ "\"password\" : \"" + newPassword +"\"},"
				+ "\"account_id\" : \"" + accountId + "\"}";
		String jsonResponse;
		jsonResponse = acrobitUserClient.unlockAcrobitsAccount(virtualNumber, requestedFields);
		
		this.sendNewPasswordNotification(email, virtualNumber, turboOfficeUserInfoResponse.getTurboOfficeUserData().getFullUserName(), newPassword);
		JSONObject objectJson = new JSONObject(jsonResponse);
		String result = objectJson.getString("result");
		if (!result.toUpperCase().equals(ResponseCodeConstants.SUCCESS_MESSAGE)) {
			throw new TrException("No pudo ser desbloqueada la cuenta del APP(acrobits)");
		}
	}
  
	private Long getAcrobitsAccountId(String virtualNumber) throws Exception {
		Long accountId = null;
		String requestedFields = "{\"requested_fields\" : \"account_id\", "
				+ "\"cloud_username\" : \"" + virtualNumber + "\"}";
		String jsonResponse;
		try {
			jsonResponse = acrobitUserClient.getUserAccount(virtualNumber, requestedFields);
			JSONObject objectJson = new JSONObject(jsonResponse);
			accountId = objectJson.getLong("account_id");
		} catch (Exception e){
			logger.error("Error al obtener account id desde acrobits para el usuario " + virtualNumber);
			throw new TrException("No ha sido posible consultar el Id de acrobits.");
		}
		return accountId;
	}
	
	public void sendNewPasswordNotification(String email, String cellphoneNumber, String customerName, String newPassword) {
		if (StringUtils.isNotBlank(email)) {
			try {
				
				String message = "Tu cuenta ha sido reiniciada, ingresa nuevamenete a Turbored App   con usuario: " + cellphoneNumber + " y contraseña: " + newPassword;
				emailService.sendSimpleAutamaticFormatNotification(customerName, cellphoneNumber, email,
						"Turbo office-reinicio de cuenta ", message, null);
			} catch (Exception e) {
				logger.info("La notificación  " + cellphoneNumber + " no pudo ser enviada por correo a " + email
						+ " Error msg " + e.getMessage());
			}
		}
	}
	
	private String generateNewPassword() {
		int length = 10;
	    boolean useLetters = true;
	    boolean useNumbers = false;
	    String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
	    return generatedString;
	}
	
	public void sendSmsCodeToValidateIndentity(String  virtualNumber) throws Exception {
		if(StringUtils.isNotBlank(virtualNumber)) {
			TurboOfficeUserInfoResponse turboOfficeUserInfoResponse = this.turboApiClient.getUserInfo(virtualNumber);
			if(turboOfficeUserInfoResponse != null) {
				TurboOfficeUserData turboOfficeUserData = turboOfficeUserInfoResponse.getTurboOfficeUserData();
				String mobileInstallNumber = turboOfficeUserData.getMobileInstallNumber();
				logger.info("mobileInstallNumber: " + mobileInstallNumber  + " for virtual number " + virtualNumber);
				UserValidationCodeRequest userValidationCodeRequest = new UserValidationCodeRequest();
				userValidationCodeRequest.setCellphoneNumber(mobileInstallNumber);
				userValidationCodeRequest.setOrigin(TurboOfficeConstants.TURBO_OFFICE_IDENTITY);
				turboApiClient.sendUserCodeBySmsWs(userValidationCodeRequest);
				logger.info("Codigo enviado");
			} else {
				throw new TrException("Numero no encontrado.");
			}
		} else {
			throw new TrException("El numero virtual es requerido");
		}
	}
	
	public void validateUserCode(String virtualNumber, String codeValidation) throws Exception {
		logger.info("Validar codigo de numero virtual " + codeValidation + "-" + virtualNumber);
		TurboOfficeUserInfoResponse turboOfficeUserInfoResponse = this.turboApiClient.getUserInfo(virtualNumber);
		if(turboOfficeUserInfoResponse != null) {
			UserValidationCodeRequest userValidationCodeRequest = new UserValidationCodeRequest();
			TurboOfficeUserData turboOfficeUserData = turboOfficeUserInfoResponse.getTurboOfficeUserData();
			String mobileInstallNumber = turboOfficeUserData.getMobileInstallNumber();
			userValidationCodeRequest.setCellphoneNumber(mobileInstallNumber);
			userValidationCodeRequest.setCode(codeValidation);
			userValidationCodeRequest.setOrigin(TurboOfficeConstants.TURBO_OFFICE_IDENTITY);
			GeneralResponse generalResponse = turboApiClient.validateUserCode(userValidationCodeRequest);
			logger.info("Codigo validado " + generalResponse.getCode() + "-" + generalResponse.getMessage() );
		}
	}

	public BindingResult validatePaymentLink(TurboOfficePaymentLinkModel turboOfficePaymentLinkModel, BindingResult bindingResult) {
        String link = turboOfficePaymentLinkModel.getLink();
        if (StringUtils.isBlank(link)) {
            bindingResult.rejectValue("link", "mandatory.field");
        }

        if (!GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER,
                turboOfficePaymentLinkModel.getMsisdn())) {
            bindingResult.rejectValue("msisdn", "msisdn.number.then");
        }
        return  bindingResult;
    }

    public String linkPago(TurboOfficePaymentLinkModel turboOfficePaymentLinkModel) throws Exception {

        logger.info("Link Pago");
        PaymentLinkNotificationRequest paymentLinkNotificationRequest = new PaymentLinkNotificationRequest();
        paymentLinkNotificationRequest.setCellphoneNumber(turboOfficePaymentLinkModel.getMsisdn());
        paymentLinkNotificationRequest.setPaymentLink(turboOfficePaymentLinkModel.getLink());
        paymentLinkNotificationRequest.setOrigin("TURBO_OFFICE_ACTIVATION");
        GeneralResponse generalResponse = turboApiClient.linkPayment(paymentLinkNotificationRequest);
        logger.info("Codigo" + generalResponse.getCode() + "-" + generalResponse.getMessage() );
        return generalResponse==null?"Ocurrio un error":generalResponse.getMessage();
    }


    public void desvincularNumeroVirtual(String virtualNumberRequest, String associatedNumber) throws Exception {

        try {

            VirtualNumber virtualNumber = this.getVirtualNumber(virtualNumberRequest);
            virtualNumber.setStatus("AVAILABLE");
            virtualNumber.setTurboOfficeUserId(null);
            virtualNumber.setUpdatedAt(new Date());
            this.saveVirtualNumber(virtualNumber);

            TurboOfficeUser turboOfficeUser = this.getTurboOfficeUser(associatedNumber);
            //turboOfficeUser.setVirtualNumberObj(null);
            turboOfficeUser.setVirtualNumber(null);
            turboOfficeUser.setMobileInstallNumber(null);
            turboOfficeUser.setUpdatedAt(new Date());
            turboOfficeUser.setCallFowarding(0);

            this.saveTOfficeUser(turboOfficeUser);


            //doUnFoward
            if(turboOfficeUser.getCallFowarding() == 1) {
                ManagementServiceRequest managementServiceRequest = new ManagementServiceRequest();
                managementServiceRequest.setMsisdn(turboOfficeUser.getVirtualNumber());
                managementServiceRequest.setCallForwarding(String.valueOf(0));
                managementServiceRequest.setMsisdn_callForwarding(turboOfficeUser.getAssociatedNumber());
                gategayApiClient.doFoward(managementServiceRequest);
            }




        } catch (Exception e) {
            logger.info("Se ha generado un error al borrar el registro en Turbo office" + e.getMessage());
            throw new TrException("Se ha generado un error al desvincular  el registro en Turbo office, intentalo mas tarde. ");
        }
    }
	
	
	 public TurboOfficeUser getTurboOfficeUser(String associatedNumber){
        return
                toOfficeUserRepository.findByAssociatedNumber(associatedNumber);
    }

    public VirtualNumber getVirtualNumber(String  virtualNumber){
        return
                virtualNumberRepository.findByNumber(virtualNumber);
    }

    public TurboOfficeUser saveTOfficeUser(TurboOfficeUser turboOfficeUser){
        return
                toOfficeUserRepository.save(turboOfficeUser);
    }

    public VirtualNumber saveVirtualNumber(VirtualNumber virtualNumber){
        return
                virtualNumberRepository.save(virtualNumber);
    }
	
  
}
