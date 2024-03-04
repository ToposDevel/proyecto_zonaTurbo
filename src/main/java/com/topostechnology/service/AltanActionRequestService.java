package com.topostechnology.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.topostechnology.domain.AltanActionRequest;
import com.topostechnology.repository.AltanActionRequestRespository;
import com.topostechnology.rest.client.response.AltanActionResponse;
import com.topostechnology.utils.StringUtils;

@Service
public class AltanActionRequestService {
	
	private static final Logger logger = LoggerFactory.getLogger(AltanActionRequestService.class);
	
	private static final int REQUEST_RESPONSE_MAX_SIZE = 300;
	
	@Autowired
	private AltanActionRequestRespository altanActionRequestRespository;
	
	
	public void save(String cellphoneNumber, AltanActionResponse altanActionResponse) {
		logger.info("Creando registro AltanActionRequest  en bd para el número celular " + cellphoneNumber);
		AltanActionRequest altanActionRequest = new AltanActionRequest();
		altanActionRequest.setActive(true);
		altanActionRequest.setCellphoneNumber(cellphoneNumber);
		
		altanActionRequest.setJsonRequest(StringUtils.getSubstring(altanActionResponse.getJsonRequest(), REQUEST_RESPONSE_MAX_SIZE));
		altanActionRequest.getJsonRequest().trim();
		altanActionRequest.setJsonResponse(StringUtils.getSubstring(altanActionResponse.getJsonResponse(),REQUEST_RESPONSE_MAX_SIZE));
		altanActionRequest.getJsonResponse().trim();
		altanActionRequest.setStatus(altanActionResponse.getStatus());
		altanActionRequest.setUrl(altanActionResponse.getUrl());
		altanActionRequest.setCreatedAt(new Date());
		altanActionRequestRespository.save(altanActionRequest);
		logger.info("AltanActionRequest registrado en bd con Id " + altanActionRequest.getId() + " para el número celular " + cellphoneNumber);
	}

}
