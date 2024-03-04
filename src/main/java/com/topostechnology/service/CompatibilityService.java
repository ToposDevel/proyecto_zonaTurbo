package com.topostechnology.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.topostechnology.model.CompatibilityModel;
import com.topostechnology.rest.client.GategayApiClient;
import com.topostechnology.utils.StringUtils;

@Service
public class CompatibilityService {

	private static final Logger logger = LoggerFactory.getLogger(CompatibilityService.class);

	private static final String HOMOLOGATED_OR_VOLTE = "HOMOLOGADOS O VOLTE";
	private static final String VOZAPP = "VOZAPP 2.0.3";
	private static final String VOZAPPG = "VOZAPP";
	private static final String COMPATIBLEM = "COMPATIBLE";
	private static final String NO_COMPATIBLE = "NO COMPATIBLE";
	private static final String NOT_FOUND = "INFORMACIÓN NO ENCONTRADA";
	private static final String COMPATIBLE = "¡Felicidades! Tu telefono es compatible con la nueva red.";
	private static final String NO_COMPATIBLE_MESSAGE = "Tu celular no es compatible, pero no te preocupes "
			+ "tenemos planes alternativos mientras cambias tu celular.";
	private static final String COMPATIBLE_VOZAPP = "Felicidades, tu celular es compatible con la nueva red,"
			+ "para hacer uso de voz sobre LTE solo descarga la aplicación VozApp";
	private static final String NO_ENCONTRADO_MESSAGE = "No encontramos tu imei, verifica que sea correcto "
			+ "o comunicate con nosotros para que te ayudemos.";


	@Autowired
	private GategayApiClient gategayApiClient;
	
	@Value("${compatibility.compatible.options}")
	private String compatibleOptUrl;
	
	@Value("${compatibility.nocompatible.options}")
	private String noCompatibleOptUrl;
	
	@Value("${compatibility.vozapp.url}")
	private String vozappUrl;
	
	@Value("${compatibility.vozapp.googleplay}")
	private String googleplayUrl;
	
	public CompatibilityModel checkImei(String imei) throws Exception {
		logger.info("Verificando imei " + imei);
		CompatibilityModel compatibilityModel = new CompatibilityModel();
		String homologated = gategayApiClient.checkImei(imei);
		logger.info("Homologated imei "  + imei + " " + homologated);
		if (homologated != null) {
			String homologatedUpp = homologated.toUpperCase();
			if (homologatedUpp.equals(HOMOLOGATED_OR_VOLTE) || homologatedUpp.equals(COMPATIBLEM)) {
				compatibilityModel.setMessage(COMPATIBLE);
				compatibilityModel.setCompatibleOptUrl(compatibleOptUrl);
				compatibilityModel.setCompatible(true);
			} else if (homologatedUpp.equals(VOZAPP) || homologatedUpp.equals(VOZAPPG)) {
				compatibilityModel.setMessage(COMPATIBLE_VOZAPP);
				compatibilityModel.setCompatibleOptUrl(compatibleOptUrl);
				compatibilityModel.setVozappUrl(vozappUrl);
				compatibilityModel.setGoogleplayUrl(googleplayUrl);
				compatibilityModel.setVoiceApp(true);
			} else if (homologatedUpp.equals(NO_COMPATIBLE)) {
				compatibilityModel.setNoCompatibleOptUrl(noCompatibleOptUrl);
				compatibilityModel.setMessage(NO_COMPATIBLE_MESSAGE);
				compatibilityModel.setNoCompatible(true);
			} else if (homologatedUpp.equals(NOT_FOUND)) {
				compatibilityModel.setNoCompatibleOptUrl(noCompatibleOptUrl);
				compatibilityModel.setMessage(NO_ENCONTRADO_MESSAGE);
				compatibilityModel.setNoCompatible(true);
			}
		}
		compatibilityModel.setImei(imei);
		return compatibilityModel;
	}

	public void validateImei(CompatibilityModel compatibilityModel, BindingResult bindingResult) {
		String imei = compatibilityModel.getImei();
		if (StringUtils.isBlank(imei)) {
			bindingResult.rejectValue("imei", "mandatory.field");
		} else if (imei.length() != 15) {
			bindingResult.rejectValue("imei", "imei.number.fifteen");
		}
	}

}
