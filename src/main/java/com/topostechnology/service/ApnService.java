package com.topostechnology.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.topostechnology.rest.client.GategayApiClient;

@Service
public class ApnService {

	private static final Logger logger = LoggerFactory.getLogger(ApnService.class);

	@Autowired
	private GategayApiClient gategayApiClient;
	
	public void changeApn(String phone) throws Exception {
		logger.info("Verificando imei " + phone);
		gategayApiClient.apnChange(phone);
	}

}
