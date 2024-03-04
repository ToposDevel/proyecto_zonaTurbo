package com.topostechnology.service;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.topostechnology.constant.UserConstants;
import com.topostechnology.domain.PlantingSim;
import com.topostechnology.domain.PlantingSimParticipant;
import com.topostechnology.exception.TrException;
import com.topostechnology.model.ActivateSimModel;
import com.topostechnology.model.ValidateImeiAndActivateSimModel;
import com.topostechnology.model.WinSimSurveyModel;
import com.topostechnology.repository.PlantingSimRepository;
import com.topostechnology.repository.SimParticipantRepository;
import com.topostechnology.rest.client.GategayApiClient;
import com.topostechnology.rest.client.response.AltanActionResponse;
import com.topostechnology.rest.client.response.SubscribersActivateRequest;
import com.topostechnology.utils.DateUtils;
import com.topostechnology.utils.StringUtils;
import com.topostechnology.validation.GeneralValidator;

@Service
public class PlantingSimService {
	
	private static final Logger logger = LoggerFactory.getLogger(PlantingSimService.class);
	
	private static final String HOMOLOGATED_OR_VOLTE = "HOMOLOGADOS O VOLTE";
	private static final String SUCCESS_MESSAGE = "¡FELICIDADES! POR FAVOR PASA POR TU SIM CON NUESTRO REPRESENTANTE Y DISFRUTA DE 7 DIAS DE SERVICIO ILIMITADO.";
	private static final String NOT_COMPATIBLE_MESSAGE = "Su dispositivo móvil NO es VoLTE con lo cual no podemos activarle su Plan. Por favor introduzca otro IMEI que sea compatible.";
	private static final String PLANTING_SIM_PARTICIPANT_WON_STATUS = "GANO_SIM";
	private static final String PLANTING_SIM_PARTICIPANT_SIM_ACTIVATED_STATUS = "GANO_SIM";
	private static final String PLANTING_SIM_PARTICIPANT_NOT_COMPATIBLE_STATUS = "IMEI_NO_COMPATIBLE";
	private static final String PLANTING_SIM_ACTIVATED_STATUS = "ACTIVADO";
	private static final String PARTICIPANT_PRINCIPAL_TYPE = "PRINCIPAL";
	private static final String WIN_SIM_PROMOCION = "la promoción de SIMS";
	private static final String ZERO = "0";

	@Autowired
	private GategayApiClient gategayApiClient;

	@Autowired
	private SimParticipantRepository simParticipantRepository;

	@Autowired
	private PlantingSimRepository plantingSimRepository;

	@Value("${planting.sim.offerid}")
	private String plantingOfferId;
	
	@Value("${planting.sim.days.offerid}")
	private String plantingOfferIdDays;

	public String proccessSurvey(WinSimSurveyModel winSimSurveyModel) throws Exception {
		String message = "";
		String status = "";
		if (this.checkImei(winSimSurveyModel.getImei())) {
			message = SUCCESS_MESSAGE;
			status = PLANTING_SIM_PARTICIPANT_WON_STATUS;
		} else {
			message = NOT_COMPATIBLE_MESSAGE;
			status = PLANTING_SIM_PARTICIPANT_NOT_COMPATIBLE_STATUS;
		}
		this.saveSurveyInfo(winSimSurveyModel, status);
		return message;
	}
	
	public String getActivateSimMessage(ActivateSimModel activateSimModel) throws Exception {
		String activationSimMessage = null;
		if (activateSimModel != null && activateSimModel.getActivationOrderId() != null) {
			activationSimMessage = "El SIM con número ICCID " + activateSimModel.getIccid() + " ha sido activado exitosamente"
					+ " con el número celular " + activateSimModel.getCellphoneNumberActivated();
			logger.info("message: " + activationSimMessage);
		} else {
			throw new TrException("Se ha generado un error al activar el sim con número iccid"
					+ activateSimModel.getIccid() + activateSimModel.getIccid() + ", contacte al área de soporte.");
		}
		return activationSimMessage;
	}
	
	public void activateSim(ActivateSimModel activateSimModel) throws Exception {
		if (StringUtils.isNotBlank(activateSimModel.getIccid())) {
			String cellphoneNumberActivated = this.getCellphoneNumberByIccid(activateSimModel.getIccid());
			SubscribersActivateRequest subscriberActivateRequest = createPrepaidOfferActivateRequest();
			AltanActionResponse altanActionResponse = gategayApiClient.activateMsisdn(cellphoneNumberActivated, subscriberActivateRequest);// TODO
			activateSimModel.setActivationOrderId(altanActionResponse.getAltanOrderId());
			activateSimModel.setCellphoneNumberActivated(cellphoneNumberActivated);
			updatePlantingSim(activateSimModel.getIccid(), activateSimModel.getActivationOrderId());
		} else {
			throw new TrException("El número iccid " + activateSimModel.getIccid() + "es requerido");
		}
	}

	public boolean checkImei(String imei) throws Exception {
		String homologated = gategayApiClient.checkImei(imei);
		boolean valid = false;
		if(homologated != null){
			valid = (homologated.toUpperCase()).equals(HOMOLOGATED_OR_VOLTE);
		}
		return valid;
	}

	public BindingResult validateWinSimSurveyData(WinSimSurveyModel winSimSurveyModel, BindingResult bindingResult) {
		if (StringUtils.isBlank(winSimSurveyModel.getFullName())) {
			bindingResult.rejectValue("fullName", "mandatory.field");
		}
		if (StringUtils.isBlank(winSimSurveyModel.getEmail())) {
			bindingResult.rejectValue("email", "mandatory.field");
		}
		if (!GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER,
				winSimSurveyModel.getCellphoneNumber())) {
			bindingResult.rejectValue("cellphoneNumber", "cellphone.number.then");
		}
		if (!GeneralValidator.validatePattern(UserConstants.REGEX_IMEI_NUMBER, winSimSurveyModel.getImei())) {
			bindingResult.rejectValue("imei", "imei.number.fifteen");
		}
		return bindingResult;
	}

	public BindingResult validateActivateSimData(ActivateSimModel activateSimModel, BindingResult bindingResult) {
		if (StringUtils.isBlank(activateSimModel.getIccid())) {
			bindingResult.rejectValue("iccid", "mandatory.field");
		}
		return bindingResult;
	}

	public void saveSurveyInfo(WinSimSurveyModel winSimSurveyModel, String status) {
		PlantingSimParticipant simParticipant = new PlantingSimParticipant();
		simParticipant.setType(PARTICIPANT_PRINCIPAL_TYPE);
		simParticipant.setFullName(winSimSurveyModel.getFullName());
		simParticipant.setCellphoneNumber(winSimSurveyModel.getCellphoneNumber());
		simParticipant.setImei(winSimSurveyModel.getImei());
		simParticipant.setEmail(winSimSurveyModel.getEmail());
		simParticipant.setStatus(status);
		Date date = new Date(Calendar.getInstance().getTime().getTime());
		simParticipant.setCreatedAt(date);
		simParticipant.setActive(true);
		simParticipantRepository.save(simParticipant);
	}
	
	public void cleanWinSimSurveyData(WinSimSurveyModel winSimSurveyModel) {
		winSimSurveyModel.setCellphoneNumber("");
		winSimSurveyModel.setEmail("");
		winSimSurveyModel.setImei("");
		winSimSurveyModel.setFullName("");
	}

	public void cleanActivateSimData(ActivateSimModel activateSimModel) {
		activateSimModel.setIccid("");
	}
	
	public String validateImeiAndActivateSim(ValidateImeiAndActivateSimModel  validateImeiAndActivateSimModel) throws Exception {
		String message = "";
		String status = "";
		PlantingSimParticipant simParticipant = this.saveParticipantInfo(validateImeiAndActivateSimModel, status);
		if (this.checkImei(validateImeiAndActivateSimModel.getImei())) {
			this.activateSim(validateImeiAndActivateSimModel);
			status = PLANTING_SIM_PARTICIPANT_SIM_ACTIVATED_STATUS;
			simParticipant.setSim(validateImeiAndActivateSimModel.getCellphoneNumberActivated());
			simParticipant.setStatus(status);
			this.updateParticipantInfo(simParticipant);
			message = this.getActivateSimMessage(validateImeiAndActivateSimModel);
		} else {
			message = NOT_COMPATIBLE_MESSAGE;
			status = PLANTING_SIM_PARTICIPANT_NOT_COMPATIBLE_STATUS;
			simParticipant.setStatus(status);
			this.updateParticipantInfo(simParticipant);
			throw new TrException(message);
		}
		return message;
	}
	
	public PlantingSimParticipant saveParticipantInfo(ValidateImeiAndActivateSimModel validateImeiAndActivateSimModel, String status) {
		PlantingSimParticipant simParticipant = new PlantingSimParticipant();
		simParticipant.setType(PARTICIPANT_PRINCIPAL_TYPE);
		simParticipant.setFullName(validateImeiAndActivateSimModel.getFullName());
		simParticipant.setCellphoneNumber(validateImeiAndActivateSimModel.getCellphoneNumber());
		simParticipant.setImei(validateImeiAndActivateSimModel.getImei());
		simParticipant.setEmail(validateImeiAndActivateSimModel.getEmail());
		simParticipant.setStatus(status);
		Date date = new Date(Calendar.getInstance().getTime().getTime());
		simParticipant.setCreatedAt(date);
		simParticipant.setActive(true);
		simParticipant.setSim(validateImeiAndActivateSimModel.getCellphoneNumberActivated());
		simParticipantRepository.save(simParticipant);
		return simParticipant;
	}
	
	private void updateParticipantInfo(PlantingSimParticipant simParticipant) {
		simParticipantRepository.save(simParticipant);
	}
	
	public void cleanValidateImeiAndActivateSimData(ValidateImeiAndActivateSimModel validateImeiAndActivateSimModel) {
		validateImeiAndActivateSimModel.setCellphoneNumber("");
		validateImeiAndActivateSimModel.setEmail("");
		validateImeiAndActivateSimModel.setImei("");
		validateImeiAndActivateSimModel.setFullName("");
		validateImeiAndActivateSimModel.setIccid("");
	}

	private String getCellphoneNumberByIccid(String iccid) throws TrException {
		logger.info("Consultando el número celular del iccid " + iccid);
		boolean active = true;
		PlantingSim plantingSim = plantingSimRepository.findByIccidAndActive(iccid, active);
		if (plantingSim == null) {
			throw new TrException("El número ICCID " + iccid + " no pertenece a " + WIN_SIM_PROMOCION);
		} else if (plantingSim.getStatus().equals(PLANTING_SIM_ACTIVATED_STATUS)) {
			logger.info("Número celular encontrado: "  + plantingSim.getCellphoneNumber() + "ya esta activado");
			throw new TrException("El número ICCID " + iccid + " ya fue activado anteriormente");
		}else if(!plantingSim.isActive()) {
			logger.info("Número celular no disponible para activación "  + plantingSim.getCellphoneNumber());
			throw new TrException("El número ICCID ya no se encuentra disponible para activación");
		} else {
			logger.info("Número celular encontrado: "  + plantingSim.getCellphoneNumber());
			return plantingSim.getCellphoneNumber();
		}
	}

	private SubscribersActivateRequest createPrepaidOfferActivateRequest() {
		SubscribersActivateRequest subscriberActivateRequest = new SubscribersActivateRequest();
		subscriberActivateRequest.setOfferingId(plantingOfferId);
		if(!plantingOfferIdDays.equals(ZERO)) {
			Date todayDate = new Date();
			String startEffectiveDateFormatted = DateUtils.formatDate(todayDate, DateUtils.YYYY_MM_DD_FORMAT);
			subscriberActivateRequest.setStartEffectiveDate(startEffectiveDateFormatted);
			Date expireEffectiveDate = DateUtils.addOrRemoveDays(todayDate, Integer.valueOf(plantingOfferIdDays));
			String expireEffectiveDateFormatted = DateUtils.formatDate(expireEffectiveDate, DateUtils.YYYY_MM_DD_FORMAT);
			subscriberActivateRequest.setExpireEffectiveDate(expireEffectiveDateFormatted);
		}
		return subscriberActivateRequest;
	}

	private void updatePlantingSim(String iccid, String activationOrderId) {
		boolean active = true;
		PlantingSim plantingSim = plantingSimRepository.findByIccidAndActive(iccid, active);
		plantingSim.setActivationOrderId(activationOrderId);
		plantingSim.setStatus(PLANTING_SIM_ACTIVATED_STATUS);
		plantingSim.setUpdatedAt(new Date());
		plantingSimRepository.save(plantingSim);
	}
	
	public BindingResult validateImeiAndActivateSimData(ValidateImeiAndActivateSimModel validateImeiAndActivateSimModel, BindingResult bindingResult) {
		if (StringUtils.isBlank(validateImeiAndActivateSimModel.getFullName())) {
			bindingResult.rejectValue("fullName", "mandatory.field");
		}
		if (StringUtils.isBlank(validateImeiAndActivateSimModel.getEmail())) {
			bindingResult.rejectValue("email", "mandatory.field");
		}
		if (!GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER,
				validateImeiAndActivateSimModel.getCellphoneNumber())) {
			bindingResult.rejectValue("cellphoneNumber", "cellphone.number.then");
		}
		if (!GeneralValidator.validatePattern(UserConstants.REGEX_IMEI_NUMBER, validateImeiAndActivateSimModel.getImei())) {
			bindingResult.rejectValue("imei", "imei.number.fifteen");
		}
		if (StringUtils.isBlank(validateImeiAndActivateSimModel.getIccid())) {
			bindingResult.rejectValue("iccid", "mandatory.field");
		}

		return bindingResult;
	}

}
