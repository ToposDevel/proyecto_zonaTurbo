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

import com.topostechnology.rest.client.request.SendEmailRequest;
import com.topostechnology.rest.client.response.GeneralResponse;
@Service
public class TurpoEmailManagerApiClient extends BaseApiClient {
	
	private static final Logger logger = LoggerFactory.getLogger(TurpoEmailManagerApiClient.class);

	@Value("${turboemailmanagerapi.url}")
	private String turboEmailManagerApiUrl;

	@Value("${turboemailmanagerapi.sendAutomaticFormatNotification.ws}")
	private String sendAutomaticFormatNotificationWs;
	
	@Value("${turboemailmanagerapi.sendEmailWithTemplate}")
	private String sendEmailWithTemplateWs;
	
	@Value("${turboemailmanagerapi.user}")
	private String turboemailmanagerapiUser;
	
	@Value("${turboemailmanagerapi.password}")
	private String turboemailmanagerapiPass;
	
	public GeneralResponse sendAutomaticFormatNotificationWs(SendEmailRequest sendEmailRequest) throws Exception { 
		logger.info("Enviando correo  con standar template a traves de TurboEmailManagerAPI");
		GeneralResponse generalResponse = null;
		String url = turboEmailManagerApiUrl + sendAutomaticFormatNotificationWs ;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBasicAuth(turboemailmanagerapiUser, turboemailmanagerapiPass);
		HttpEntity<SendEmailRequest> request  = new HttpEntity<>(sendEmailRequest, headers);
		RestTemplate restTemplate = this.getRestTemplate();
		ResponseEntity<GeneralResponse> response = restTemplate.exchange(url, HttpMethod.POST, request,
				GeneralResponse.class);
		int status = response.getStatusCodeValue();
		if (status == 200) {
			generalResponse = response.getBody();
		} else {
			logger.error("Error notificacion NO pudo ser enviada status => " + status);
		}
		return generalResponse;
	}
	
	public GeneralResponse sendEmailWithTemplateWs(SendEmailRequest sendEmailRequest) throws Exception { 
		logger.info("Enviando correo  con template a traves de TurboEmailManagerAPI");
		GeneralResponse generalResponse = null;
		String url = turboEmailManagerApiUrl + sendEmailWithTemplateWs ;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBasicAuth(turboemailmanagerapiUser, turboemailmanagerapiPass);
		HttpEntity<SendEmailRequest> request  = new HttpEntity<>(sendEmailRequest, headers);
		RestTemplate restTemplate = this.getRestTemplate();
		ResponseEntity<GeneralResponse> response = restTemplate.exchange(url, HttpMethod.POST, request,
				GeneralResponse.class);
		int status = response.getStatusCodeValue();
		if (status == 200) {
			generalResponse = response.getBody();
		} else {
			logger.error("Error notificacion NO pudo ser enviada status => " + status);
		}
		return generalResponse;
	}
	
}	
