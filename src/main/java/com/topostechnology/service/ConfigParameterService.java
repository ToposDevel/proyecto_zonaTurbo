package com.topostechnology.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.topostechnology.domain.ConfigParameter;
import com.topostechnology.repository.ConfigParameterRepository;
import com.topostechnology.utils.StringUtils;

@Service
public class ConfigParameterService {
	
	private static final Logger logger = LoggerFactory.getLogger(ConfigParameterService.class);
	
	@Autowired
	private ConfigParameterRepository configParameterRepository;
	
	public ConfigParameter getConfigParameter(String name) {
		ConfigParameter configParameter = null;
		if(StringUtils.isNotBlank(name)) {
			configParameter = configParameterRepository.findByName(name);
		} else {
			logger.error("Name is blank or null");
		}
		return configParameter;
	}
	
	public String getParameterValue(String name) {
		logger.info("Consultando valor de parametro " + name);
		String value = null;
		if(StringUtils.isNotBlank(name)) {
			ConfigParameter configParameter = configParameterRepository.findByName(name);
			if(configParameter != null) {
				value = configParameter.getValue();
			}
		} else {
			logger.error("Name is blank or null");
		}
		return value;
	}
	

}
