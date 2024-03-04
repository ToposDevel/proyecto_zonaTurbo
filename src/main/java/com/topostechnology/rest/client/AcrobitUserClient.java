package com.topostechnology.rest.client;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.topostechnology.exception.AcrobitsException;
import com.topostechnology.rest.client.request.AcrobitsChangeAcountStatusRequest;
import com.topostechnology.rest.client.request.AcrobitsCheckAcountStatusRequest;
import com.topostechnology.rest.client.request.AcrobitsGetTokenRequest;
import com.topostechnology.rest.client.request.AcrobitsUserRequest;
import com.topostechnology.rest.client.response.AcrobitsLoginResponse;

@Component
public class AcrobitUserClient {
  private static final Logger logger = LoggerFactory.getLogger(AcrobitUserClient.class);
  
  @Value("${acrobits.api.signup}")
  private String acrobitsSignupWs;
  
  @Value("${acrobits.turbored.loginWs}")
  private String turboredLoginWs;
  
  @Value("${acrobits.turbored.cloudid}")
  private String cloudId;
  
  @Value("${acrobits.turbored.user}")
  private String turboredUser;
  
  @Value("${acrobits.turbored.password}")
  private String turboredPassword;
  
  @Value("${acrobits.turbored.checkAccountStatusWs}")
  private String checkAccountStatusWs;
  
  @Value("${acrobits.turbored.changeAccountStatusWs}")
  private String changeAccountStatusWs;
  
  @Value("${acrobits.turbored.account}")
  private String accountWs;
  
  @Value("${acrobits.turbored.updateAccount}")
  private String updateAccountWs;
  
  public void register(AcrobitsUserRequest acrobitUserRequest) throws Exception {
    logger.info("Haciendo request de acrobits para registrar al usuario " + acrobitUserRequest.getUsername());
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<AcrobitsUserRequest> request = new HttpEntity(acrobitUserRequest, (MultiValueMap)headers);
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.exchange(this.acrobitsSignupWs, HttpMethod.POST, request, String.class, new Object[0]);
    int status = response.getStatusCodeValue();
    logger.info("status de response al registrar usuario " + acrobitUserRequest.getUsername() + ": " + status);
    if (status == 200) {
      String responseStr = (String)response.getBody();
      logger.info("Respuesta desde acrobits al registrar usuario " + acrobitUserRequest.getUsername() + ": " + responseStr);
    } else {
      logger.error("No se pudo registrar en acrobits al usuario " + acrobitUserRequest.getUsername());
    } 
  }
  
  public AcrobitsLoginResponse login() throws AcrobitsException {
    logger.info("Haciendo request de acrobits para obtener el token ");
    AcrobitsLoginResponse acrobitsLoginResponse = null;
    AcrobitsGetTokenRequest acrobitsGetTokenRequest = new AcrobitsGetTokenRequest(this.cloudId, this.turboredUser, this.turboredPassword);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<AcrobitsGetTokenRequest> request = new HttpEntity(acrobitsGetTokenRequest, (MultiValueMap)headers);
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.exchange(this.turboredLoginWs, HttpMethod.POST, request, String.class, new Object[0]);
    int status = response.getStatusCodeValue();
    logger.info("status de response al obtener el token: " + status);
    if (status == 200) {
      String jsonResponse = (String)response.getBody();
      JSONObject objectJson = new JSONObject(jsonResponse);
      String token = objectJson.getString("Token");
      acrobitsLoginResponse = new AcrobitsLoginResponse();
      acrobitsLoginResponse.setToken(token);
    } else {
      logger.error("No se pudo obtener el token");
    } 
    return acrobitsLoginResponse;
  }
  
  public String checkAccountStatusWs(AcrobitsCheckAcountStatusRequest checkAcountStatusRequest) throws AcrobitsException {
    logger.info("Haciendo request de acrobits para consultar estatus de cuenta " + checkAcountStatusRequest.getCloud_username());
    String accountStatus = null;
    HttpHeaders headers = new HttpHeaders();
    headers.set("token", getToken());
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<AcrobitsCheckAcountStatusRequest> request = new HttpEntity(checkAcountStatusRequest, (MultiValueMap)headers);
    RestTemplate restTemplate = new RestTemplate();
    logger.info("Requesting  " + checkAcountStatusRequest.toString());
    ResponseEntity<String> response = restTemplate.exchange(this.checkAccountStatusWs, HttpMethod.POST, request, String.class, new Object[0]);
    int status = response.getStatusCodeValue();
    logger.info("status de response al consultar estatus de cuenta " + checkAcountStatusRequest.getCloud_username());
    if (status == 200) {
      String jsonResponse = (String)response.getBody();
      JSONObject objectJson = new JSONObject(jsonResponse);
      accountStatus = objectJson.getString("status");
      logger.info("response: " + accountStatus);
    } else {
      logger.error("No se pudo consultar estatus de cuenta");
      throw new AcrobitsException("Error al consultar estatus de cuenta " + status);
    } 
    return accountStatus;
  }
  
  public void changeAccountStatus(AcrobitsChangeAcountStatusRequest changeAcountStatusRequest) throws AcrobitsException {
    logger.info("Haciendo request para actualizar estatus de cuenta " + changeAcountStatusRequest.getCloud_username());
    HttpHeaders headers = new HttpHeaders();
    headers.set("token", getToken());
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<AcrobitsChangeAcountStatusRequest> request = new HttpEntity(changeAcountStatusRequest, (MultiValueMap)headers);
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.exchange(this.changeAccountStatusWs, HttpMethod.POST, request, String.class, new Object[0]);
    int status = response.getStatusCodeValue();
    logger.info("status de response al actualizar estatus de cuenta: " + status + "Response" + (String)response.getBody());
    if (status == 200) {
      logger.info("Estatus de cuenta actualizada para " + changeAcountStatusRequest.getCloud_username());
    } else {
      logger.error("No se pudo actualizar el estatus  del usuario " + changeAcountStatusRequest.getCloud_username());
      throw new AcrobitsException("El estatus de la cuenta no pudo ser actualizado " + changeAcountStatusRequest.getCloud_username());
    } 
  }
  
	public String getUserAccount(String virtualNumber, String requestedFields) throws AcrobitsException {
		logger.info("Haciendo request para consultar a usuario " + virtualNumber);
		String jsonResponse = null;
		HttpHeaders headers = new HttpHeaders();
		headers.set("token", getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(requestedFields, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(this.accountWs, HttpMethod.POST, request, String.class);
		int status = response.getStatusCodeValue();
		logger.info("status de response al actualizar estatus de cuenta: " + status);
		if (status == 200) {
			jsonResponse = response.getBody();
		} else {
			logger.error("No se pudo consultar la cuenta del usuario " + virtualNumber);
			throw new AcrobitsException("No se pudo consultar la cuenta del usuario " + virtualNumber);
		}
		return jsonResponse;
	}
  
  public String unlockAcrobitsAccount(String virtualNumber, String requestedFields) throws AcrobitsException {
		logger.info("Haciendo request para actualizar al usuario " + virtualNumber);
		logger.info("Request: " + requestedFields);
		String jsonResponse = null;
		HttpHeaders headers = new HttpHeaders();
		headers.set("token", getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(requestedFields, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(this.updateAccountWs, HttpMethod.POST, request, String.class);
		int status = response.getStatusCodeValue();
		logger.info("status de response al actualizar la cuenta  " + virtualNumber);
		if (status == 200) {
			jsonResponse = response.getBody();
			logger.info("Response " + jsonResponse);
		} else {
			logger.error("No se pudo actualizar la cuenta del usuario " + virtualNumber);
			throw new AcrobitsException("No se pudo actualizar la cuenta del usuario " + virtualNumber);
		}
		return jsonResponse;
	}
  
  private String getToken() throws AcrobitsException {
    String token = null;
    AcrobitsLoginResponse acrobitsLoginResponse = login();
    if (acrobitsLoginResponse != null) {
      token = acrobitsLoginResponse.getToken();
    } else {
      logger.info("El token no pudo ser generado " + token);
    } 
    return token;
  }
}
