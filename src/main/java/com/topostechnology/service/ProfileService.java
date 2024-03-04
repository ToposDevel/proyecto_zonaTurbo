package com.topostechnology.service;

import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.topostechnology.constant.OfferConstans;
import com.topostechnology.constant.SuscribersConstants;
import com.topostechnology.constant.UserConstants;
import com.topostechnology.exception.TrException;
import com.topostechnology.rest.client.GategayApiClient;
import com.topostechnology.rest.client.GategayApiXtraClient;
import com.topostechnology.rest.client.response.OfferModalityResponse;
import com.topostechnology.rest.client.response.PerfilResponse;
import com.topostechnology.rest.client.response.ProfileDataResponse;
import com.topostechnology.rest.client.response.ProfileDetailOffering;
import com.topostechnology.rest.client.response.ProfileFreeUnit;
import com.topostechnology.rest.client.response.SaldoResponse;
import com.topostechnology.utils.DateUtils;
import com.topostechnology.utils.StringUtils;
import com.topostechnology.validation.GeneralValidator;

@Service
public class ProfileService {
	
	private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);
	
	public static String CELPHONE_VALIDATION_PROBLEM = " Este número celular no pudo ser validado,  intente nuevamente más tarde.";
	public static String ZERO_BALANCE = "Saldo 0";
	public static String NO_TURBORED = "  No pertenece a Turbored.";
	
	@Autowired
	private GategayApiClient gategayApiClient;
	
	@Autowired
	private GategayApiXtraClient gategayApiXtraClient;

	public Boolean isNewSim(ProfileDataResponse profile ) throws TrException {
		Boolean isNew = null;
		String profileStatus = this.getStatus(profile);
		String profileStatusUpper = profileStatus != null ? profileStatus.toUpperCase(): "";
		boolean isIdleSatus = profileStatusUpper.equals(SuscribersConstants.PROFILE_IDLE_STATUS);
		String offerId = this.getPrimaryOfferId(profile);
		boolean isDefaulActivationOffer = (profileStatusUpper.equals(SuscribersConstants.PROFILE_ACTIVE_STATUS)) && offerId.startsWith(SuscribersConstants.PREFIX_10000);
		isNew = isIdleSatus || isDefaulActivationOffer;
		if(isIdleSatus || isDefaulActivationOffer) {
			isNew = true;
		} else {
			isNew = false;
		}
		return isNew;
	}
	
	public Boolean isTurbored(String cellphoneNumber) throws TrException {
		Boolean isTurboredMsisdn = gategayApiClient.isTurboredMsisdn(cellphoneNumber);
		if(isTurboredMsisdn == null) {
			throw new TrException(cellphoneNumber + CELPHONE_VALIDATION_PROBLEM);
		}  else if(!isTurboredMsisdn) {
			throw new TrException(cellphoneNumber + NO_TURBORED);
		} 
		return isTurboredMsisdn;
	}
	
	public String getCellphoneNumberByImei(String imei) throws Exception {
		String msisdn = gategayApiClient.getDeviceInformation(imei);
    	if(msisdn == null  || !GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER, msisdn)){
    		throw new TrException("No se pudo validar el imei " + imei + " en Turbored"); // TODO VERIFICAR QUE HACER SI NO REGRESA MSISDN
    	}
		return msisdn;
	}
	
	public String getOfferId(String cellphoneNumber) throws TrException {
		String offerId = gategayApiClient.getOfferId(cellphoneNumber);
		if(StringUtils.isBlank(offerId)) {
			throw new TrException(cellphoneNumber + CELPHONE_VALIDATION_PROBLEM);
		}
		return offerId;
	}
	
	public boolean isRenewableOffer(String offerId) throws TrException {
		logger.info("Verificando si es oferta RENOVABLE");
		boolean isRenewable = false;
		OfferModalityResponse offerModalityResponse = gategayApiXtraClient.getOfferModality(offerId);
		if (offerModalityResponse == null) {
			throw new TrException(CELPHONE_VALIDATION_PROBLEM);
		} else if (!offerModalityResponse.getCodigo().equals("0")) { // 0 código de exito
			throw new TrException(CELPHONE_VALIDATION_PROBLEM);
		} else {
			String modality = offerModalityResponse.getModalidad() != null ? offerModalityResponse.getModalidad() : "";
			String modalityUpper = modality.toUpperCase();
			if (modalityUpper.equals(OfferConstans.RENEWABLE_MODALITY)) {  //ES RENOVABLE
				logger.info(offerId + " es REVOVABLE");
				isRenewable = true;
			} else if (modalityUpper.equals(OfferConstans.NO_RENEWABLE_MODALITY)) { // ES NO RENOVABLE
				logger.info(offerId + " es NO REVOVABLE");
				isRenewable = false;
			} else {
				logger.error(offerId + "  Esta oferta no pudo ser validada");
				throw new TrException(CELPHONE_VALIDATION_PROBLEM);
			}
		}
		return isRenewable;
	}
	
	public Boolean isOfferExpired(String cellphoneNumber) throws TrException {
		Boolean isExpired = null;
		Date expireDate = this.getOfferExpiredDate(cellphoneNumber);
		logger.info("Oferta de " + cellphoneNumber + " expira " + expireDate);
		if(expireDate != null) {
			Date todayDate = new Date();
			if (expireDate.after(todayDate)) {
				isExpired = false;
			} else {
				isExpired = true;
			}
		} else {
			logger.info("No se pudo obter la fecha de expiración de la oferta para el número celular " + cellphoneNumber );
			throw new TrException("La operación no pudo ser completada, intente nuevamente más tarde");
		}
		return isExpired;
	}
	
	/**
	 * Consulta la fecha de expiracion considerando(FU_Redirect_Altan-RN datos de altan para recarga)
	 * @param cellphoneNumber
	 * @return
	 * @throws TrException
	 */
	public Date getGeneralOfferExpiredDate(String cellphoneNumber) throws TrException {
		Date expireDate = null;
		try {
			ProfileFreeUnit profileFreeUnit = this.getProfileFreeUnit(cellphoneNumber);
			if (profileFreeUnit != null) {
				if (profileFreeUnit.getDetailOfferings() != null && !profileFreeUnit.getDetailOfferings().isEmpty()) {
					ProfileDetailOffering profileDetailOffering = profileFreeUnit.getDetailOfferings().get(0);
					if (profileDetailOffering != null) {
						String expireDateStr = profileDetailOffering.getExpireDate();
						expireDate = DateUtils.stringToDate(DateUtils.YYYYMMDDHHMMSS, expireDateStr);
					}
				}
			}
		} catch (Exception e) {
			logger.info("No se pudo obter la fecha de expiración de la oferta para el número celular " + cellphoneNumber + "-" +e.getMessage());
			throw new TrException("La operación no pudo ser completada, intente nuevamente más tarde");
		}
		return expireDate;
	}
	
	/**
	 * Consulta la fecha de expiración sin considerar FU_Redirect_Altan-RN 
	 * @param cellphoneNumber
	 * @return
	 * @throws TrException
	 */
	public Date getOfferExpiredDate(String cellphoneNumber) throws TrException {
		Date expireDate = null;
		try {
		SaldoResponse saldoResponse = gategayApiXtraClient.getBalanceAndExpireDate(cellphoneNumber);
		if(saldoResponse != null) {
			if(!saldoResponse.equals(ZERO_BALANCE)) {
				String expireDateStr = saldoResponse.getExpireDate();
				expireDate = DateUtils.stringToDate(DateUtils.YYYYMMDDHHMMSS, expireDateStr);
			} else {
				expireDate = new Date(); // la fecha ya expiro
			}
		} else {
			logger.error("Error saldoResponse = null");
			throw new TrException(cellphoneNumber + CELPHONE_VALIDATION_PROBLEM);
		}
		} catch (ParseException e) {
			logger.error("Error al obtener fecha de expiracion de la oferta para el número celular " + cellphoneNumber  + e.getMessage());
			throw new TrException(cellphoneNumber + CELPHONE_VALIDATION_PROBLEM);
		}
		return expireDate;
	}
	
	private ProfileFreeUnit getProfileFreeUnit(String cellphoneNumber) {
		ProfileFreeUnit profileFreeUnit = null;
		try {
		ProfileDataResponse profile = this.getProfile(cellphoneNumber);
		if(profile != null){
			logger.info(profile.getStatus().getSubStatus());
			if(profile.getFreeUnits() != null && !profile.getFreeUnits().isEmpty()) {
				profileFreeUnit = profile.getFreeUnits().get(0);
			}
		}
		} catch (Exception e) {
			logger.error("Se ha genereado un error al obtener profile freeUnit " + cellphoneNumber + "-" + e.getMessage());
		}
		return profileFreeUnit;
	}
	
	public ProfileDataResponse getProfile(String cellphoneNumber) {
		ProfileDataResponse profile = gategayApiClient.getProfile(cellphoneNumber);
		return profile;
	}
	
	public String getStatus(String cellphoneNumber) {
		String status = null;
		ProfileDataResponse profile = gategayApiClient.getProfile(cellphoneNumber);
		if (profile != null) {
			if (profile.getStatus() != null) {
				status = profile.getStatus().getSubStatus();
			}
		}
		return status;
	}
	
	public String getStatus(ProfileDataResponse profile) {
		String status = null;
		if (profile != null) {
			if (profile.getStatus() != null) {
				status = profile.getStatus().getSubStatus();
			}
		}
		return status;
	}
	
	
	public String getPrimaryOfferId(ProfileDataResponse profile) {
		String primaryOfferId = null;
		if(profile != null) {
			if(profile.getPrimaryOffering() != null) {
				primaryOfferId = profile.getPrimaryOffering().getOfferingId();
			}
		}
		return primaryOfferId;
	}
	
	public String getSuperOfferType(String cellphoneNumber) throws Exception {
		logger.info("Consultando super offer type para el número celular " +  cellphoneNumber);
		String superOfferTypeUpper = null;
		PerfilResponse perfilResponse = gategayApiXtraClient.getOfferSuperType(cellphoneNumber);
		if(perfilResponse != null) {
			String superOfferType = perfilResponse.getPerfil();
			logger.info("Super offer type " + superOfferType);
			if(!StringUtils.isBlank(superOfferType)) {
				superOfferTypeUpper = superOfferType.toUpperCase();
			} else {
				logger.info("No regreso super offer type");
				throw new TrException(cellphoneNumber + CELPHONE_VALIDATION_PROBLEM);
			}
		}
		return superOfferTypeUpper;
	}
	
}
