package com.topostechnology.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.topostechnology.exception.TrException;

@Controller
public class ConektaController {
	
	@Value("${conekta.private.api.key}")
	private String conektaPublicApiKey;
	
	private static final Logger logger = LoggerFactory.getLogger(ConektaController.class);
	
	@RequestMapping(value = "/conekta/getConektaPublicToken", method = RequestMethod.GET)
	public @ResponseBody String getConektaPublicToken() throws TrException {
		logger.info("Obteniendo llave publica conekta ");
		String conektaPublicToken = null;
		try {
			conektaPublicToken = conektaPublicApiKey;
		} catch (Exception e) {
			logger.error("Error al consultar api key publica conekta  " + e.getMessage());
			throw new TrException("La operación no pudo ser realizada, intente nuevamente más tarde");
		}
		return conektaPublicToken;
	}

}
