package com.topostechnology.rest.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.topostechnology.rest.client.request.PortingRequest;
import com.topostechnology.rest.client.response.PortingResponse;

@Service
public class Z7APIXMLPortingClient {
	
	private static final Logger logger = LoggerFactory.getLogger(Z7APIXMLPortingClient.class);
	
	@Value("${porting.api.url}")
	private String portingUrl;
	
	public PortingResponse porting(String date, String cellphoneNumber, String pin)  {
		logger.info("Haciendo requets a Z7APIXML");
		PortingResponse portingResponse = null;
		try {
			PortingRequest portingRequest = new PortingRequest(date, cellphoneNumber, pin);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<PortingRequest> request = new HttpEntity<>(portingRequest, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<PortingResponse> response = restTemplate.exchange(portingUrl, HttpMethod.POST, request,
					PortingResponse.class);
			int status = response.getStatusCodeValue();
			logger.info("Servicio de portacion response status: " + status);
			if (status == 200) {
				portingResponse = response.getBody();
				logger.info("PortiId " + portingResponse.getPortid());
			}
		} catch(Exception e) {
			logger.error("Error al procesar portacion(Z7_APIXML) para el numero celular " + cellphoneNumber );
			logger.error(e.getMessage());
		}
		
		return portingResponse;
	}

}
