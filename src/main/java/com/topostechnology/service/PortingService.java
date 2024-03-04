package com.topostechnology.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.topostechnology.constant.ProfileConstants;
import com.topostechnology.constant.ResponseCodeConstants;
import com.topostechnology.constant.UserConstants;
import com.topostechnology.exception.TrException;
import com.topostechnology.model.PortingModel;
import com.topostechnology.rest.client.GategayApiXtraClient;
import com.topostechnology.rest.client.TurboApiClient;
import com.topostechnology.rest.client.Z7APIXMLPortingClient;
import com.topostechnology.rest.client.request.SavePortingRequest;
import com.topostechnology.rest.client.request.UpdateStatusPortingRequest;
import com.topostechnology.rest.client.response.GeneralResponse;
import com.topostechnology.rest.client.response.IccidInfoResponse;
import com.topostechnology.rest.client.response.PortingResponse;
import com.topostechnology.utils.DateUtils;
import com.topostechnology.utils.StringUtils;
import com.topostechnology.validation.GeneralValidator;

@Service
public class PortingService {
	
	private static final Logger logger = LoggerFactory.getLogger(PortingService.class);
	
	public  final static String PORT_ERROR_STATUS  = "PORT_ERROR";
	
	@Autowired
	private Z7APIXMLPortingClient z7APIXMLPortingClient;
	
	@Autowired
	private TurboApiClient turboApiClient;
	
	@Autowired
	private ProfileService profileService;
	
	@Autowired
	private GategayApiXtraClient gategayApiXtraClient;
	
	@Value("${porting.rida}")
	private String rida;
	
	@Value("${porting.rcr}")
	private String rcr;
	
	@Value("${porting.autoScriptReg}")
	private String autoScriptReg;

	public String porting(PortingModel portingModel) throws Exception {
		logger.info("porting()");
		String message = "";
		String msisdnPorted = portingModel.getMsisdnPorted();
		if (!this.existPortingInProcess(msisdnPorted)) {
			IccidInfoResponse iccidInfoResponse = gategayApiXtraClient.getIccidInfo(portingModel.getIccid());
			this.validateIccidInfo(iccidInfoResponse);
			this.isSimActivated(iccidInfoResponse.getMsisdn());
			logger.info("Iccid validado exitosamente");
			String currentDateFormatted = DateUtils.getFormattedDate(new Date(), DateUtils.YYYYMMDDHHMMSS);
			PortingResponse portingResponse = z7APIXMLPortingClient.porting(currentDateFormatted, msisdnPorted,
					portingModel.getNip());
			if (portingResponse != null) {
				SavePortingRequest savePortingRequest = new SavePortingRequest();
				savePortingRequest.setDate(currentDateFormatted);
				savePortingRequest.setNip(portingModel.getNip());
				savePortingRequest.setFullName(portingModel.getFullName());
				savePortingRequest.setEmail(portingModel.getEmail());
				savePortingRequest.setMsisdnPorted(portingModel.getMsisdnPorted());
				savePortingRequest.setFolioId(portingResponse.getFolioid());
				savePortingRequest.setMessage(portingResponse.getMessage());
				savePortingRequest.setPortId(portingResponse.getPortid());
				savePortingRequest.setStatus(portingResponse.getStatus());
				savePortingRequest.setRida(rida);
				savePortingRequest.setRcr(rcr);
				savePortingRequest.setAutoScriptReg(autoScriptReg);
				savePortingRequest.setIccid(portingModel.getIccid());
				savePortingRequest.setImsi(iccidInfoResponse.getImsi());
				savePortingRequest.setMsisdnTransitory(iccidInfoResponse.getMsisdn());
				message = portingResponse.getMessage();
				logger.info("Message => " + message);
				try {
					logger.info("Registrando registro de portabilidad en BD");
					turboApiClient.savePorting(savePortingRequest);
					logger.info("Registrado exitosamente en Bd");
				} catch (Exception e) {
					logger.error("No se pudo registrar los datos en bd " + e.getMessage());
					logger.error("Error al registrar en bd con TurboApi" + e.getMessage());
				}
			} else {
				logger.error("Respuesta nula al invocar z7APIXML");
				throw new TrException("Se ha generado un error al realizar la portación del número " + msisdnPorted
						+ " intente nuevamente más tarde.");
			}
		} else {
			logger.error("Ya se encuentra una portabilidad en proceso");
			throw new TrException("Ya se encuentra en proceso la portación del número " + msisdnPorted);
		}
		return message;
	}
	
	public void getPortingStatus(PortingModel portingModel) throws Exception {
		PortingResponse portingResponse = turboApiClient.getPortingStatus(portingModel.getMsisdnPorted());
		if(portingResponse.getCode() == ResponseCodeConstants.SUCCES_CODE) {
			portingModel.setPortingDate(portingResponse.getPortingDate());
			portingModel.setMessage(portingResponse.getPortingMessage());
			portingModel.setFullName(portingResponse.getUserName());
			portingModel.setIccid(portingResponse.getIccid());
		} else {
			throw new TrException(portingResponse.getMessage());
		}
	}
	
	public void updatePortingStatus(PortingModel portingModel) {
		try {
			if(portingModel != null) {
				UpdateStatusPortingRequest updateStatusPortingRequest = new UpdateStatusPortingRequest();
				updateStatusPortingRequest.setCellphoneNumber(portingModel.getMsisdnPorted());
				updateStatusPortingRequest.setMessage(portingModel.getMessage());
				updateStatusPortingRequest.setStatus(portingModel.getStatus());
				GeneralResponse generalResponse = turboApiClient.updatePortingStatus(updateStatusPortingRequest);
				if(generalResponse.getCode() == ResponseCodeConstants.SUCCES_CODE) {
					logger.info("Estatus de portación actualizado exitosamente para el número " + portingModel.getMsisdnPorted());
				} else {
					logger.info("El estatus no pudo ser actualizado correctamente RESPONSE CODE " + generalResponse.getCode());
				}
			}
			else {
				logger.info("El estatus no pudo ser actualizado correctamente porting model es NULL");
			}
		} catch(Exception ex){
			logger.error("Error al actualizar estatus de portabilidad para el número " +portingModel.getMsisdnPorted() + ex.getMessage());
		}
	}
	
	public boolean existPortingInProcess(String cellphoneNumber) throws Exception {
		logger.info("Verificando si existe Portabilida en proceso para el número " + cellphoneNumber);
		boolean exist = false;
		PortingResponse portingResponse = turboApiClient.getPortingStatus(cellphoneNumber);
		if(portingResponse.getCode() == ResponseCodeConstants.SUCCES_CODE && !portingResponse.getStatus().equals(PORT_ERROR_STATUS)) {
			exist = true;
			logger.info("Ya existe portabilidad en proceso para el numero " + cellphoneNumber);
		} else {
			logger.info("No existe portabilidad en proceso para el numero " + cellphoneNumber);
		}
		return exist;
	}

	public BindingResult validatePortingData(PortingModel portingModel, BindingResult bindingResult) {
		
		if (StringUtils.isBlank(portingModel.getFullName())) {
			bindingResult.rejectValue("fullName", "mandatory.field");
		}
		
		if (StringUtils.isBlank(portingModel.getEmail())) {
			bindingResult.rejectValue("mail", "mandatory.field");
		} 
		if (!GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER,
					portingModel.getMsisdnPorted())) {
				bindingResult.rejectValue("msisdnPorted", "cellphone.number.then");
		}
		if (StringUtils.isBlank(portingModel.getNip())) {
			bindingResult.rejectValue("nip", "mandatory.field");
		}
		
		if (StringUtils.isBlank(portingModel.getIccid())) {
			bindingResult.rejectValue("iccid", "mandatory.field");
		}
		return bindingResult;
	}
	
	public BindingResult validateCellphoneNumber(PortingModel portingModel, BindingResult bindingResult) {
		if (!GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER,
					portingModel.getMsisdnPorted())) {
				bindingResult.rejectValue("msisdnPorted", "cellphone.number.then");
		}
		return bindingResult;
	}
	
	public void cleanPortingData(PortingModel portingModel) {
		portingModel.setFullName("");
		portingModel.setEmail("");
		portingModel.setMsisdnPorted("");;
		portingModel.setIccid("");
		portingModel.setNip("");
	}
	
	private void validateIccidInfo(IccidInfoResponse iccidInfoResponse) throws TrException {
		int zero = ResponseCodeConstants.ERROR_CODE;
		if(iccidInfoResponse == null ) {
			logger.error("No se pudo consultar la información del ICCID ingresado");
			throw new TrException("No se pudo consultar la información del ICCID ingresado, intente nuevamente más tarde.");
		} else if(iccidInfoResponse.getCodigo().equals(String.valueOf(zero))){
			logger.error("Error al validar Iccid " + iccidInfoResponse.getMensaje());
			throw new TrException(iccidInfoResponse.getMensaje());
		} else {
			logger.info("Iccid valdiado correctamente imsi " + iccidInfoResponse.getImsi());
		}
	}
	
	private void isSimActivated(String cellphoneNumber) throws TrException {
		String status = profileService.getStatus(cellphoneNumber);
		if(!status.equals(ProfileConstants.ACTIVE_STATUS)) {
			logger.error("El sim aun no esta activado: " + cellphoneNumber);
			throw new TrException("Para poder realizar la portabilidad el sim debe estar activado.");
		}
	}

}

