package com.topostechnology.rest.client;

import java.util.Collections;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.topostechnology.constant.ResponseCodeConstants;
import com.topostechnology.exception.TrException;
import com.topostechnology.rest.client.request.ManagementServiceRequest;
import com.topostechnology.rest.client.request.PurchaseRequest;
import com.topostechnology.rest.client.request.SendSmsRequest;
import com.topostechnology.rest.client.request.UpdateOfferRequest;
import com.topostechnology.rest.client.response.AltanActionResponse;
import com.topostechnology.rest.client.response.ApiGatewayGeneralResponse;
import com.topostechnology.rest.client.response.CoverageResponse;
import com.topostechnology.rest.client.response.GeneralResponse;
import com.topostechnology.rest.client.response.ImeiStatusResponse;
import com.topostechnology.rest.client.response.ManagementServiceResponse;
import com.topostechnology.rest.client.response.ProfileDataResponse;
import com.topostechnology.rest.client.response.ProfileResponse;
import com.topostechnology.rest.client.response.PurchaseResponse;
import com.topostechnology.rest.client.response.SearchResponse;
import com.topostechnology.rest.client.response.SelfConsumption;
import com.topostechnology.rest.client.response.SubscriberCoordinates;
import com.topostechnology.rest.client.response.SubscribersActivateRequest;
import com.topostechnology.rest.client.response.SubscribersActivateResponse;
import com.topostechnology.rest.client.response.SuscribersGeneralResponse;
import com.topostechnology.rest.client.response.UpdateOfferResponse;


@Service
public class GategayApiClient {

	private static final Logger logger = LoggerFactory.getLogger(GategayApiClient.class);

	private static final String BUCKET_TEXT = "buckets";
	private static final String QUESTION_MARK = "?";
	private static final String SLASH = "/";
	private static final String BE_ID = "beId";
	private static final String EQUAL = "=";

	private static final String SUSCRIBER_DOESNT_EXIS = "1211000305";
	private static final String ERROR_CODE_400 = "400";
	private static final String ERROR_CODE_400_DESCRIPTION = "The subscriber has a primary offering assigned.";
	private static final String ERROR_CODE_400_APN_CHANGE_IN_PROCESS = "Ya existe una actualización de APN enviada previamente, es necesario esperar 24 horas para volver a ejecutar esta API";
	private static final String VERSION = "version";
	
	private static final String NOT_FOUND = "INFORMACIÓN NO ENCONTRADA";

	@Value("${apigateway.cameldemo.api.url}")
	private String apiGatewayUrl;
	
	@Value("${cameldemo.api.url}")
	private String cameldemoApiUrl;
	
	@Value("${turbored.be.id}")
	private Short beId;

	@Value("${apiGateway.selfconsumpion.ws}")
	private String selfconsumpionWs;

	@Value("${apiGateway.subcribers.ws}")
	private String subscribers;

	@Value("${apigateway.subscribers.profile.ws}")
	private String profileWs;

	@Value("${apigateway.subcribers.barring.ws}")
	private String barringWs;

	@Value("${apigateway.subcribers.unbarring.ws}")
	private String unbarringWs;

	@Value("${apigateway.subcribers.suspend.ws}")
	private String suspendWs;

	@Value("${apigateway.subcribers.resume.ws}")
	private String resumeWs;

	@Value("${apigateway.serviceability.ws}")
	private String serviceabilityWs;

	@Value("${apigateway.getDeviceInformation.ws}")
	private String deviceInformationWs;

	@Value("${apigateway.imeis.ws}")
	private String imeisWs;

	@Value("${apigateway.imeis.status.ws}")
	private String imeiStatusWs;

	@Value("${apigateway.subcribers.activatesim.ws}")
	private String activateSimWs;

	@Value("${apigateway.imei.status.version.param}")
	private String imeiStatusVersionParam;
	
	@Value("${apigateway.citi.api.expressRecharge.purchase.ws}")
	private String expressRechargePurchaseWs;
	
	@Value("${apigateway.subcribers.subscriberCoordinate.ws}")
	private String subscriberCoordinateWs;
	
	@Value("${apigateway.fowarding.ws}")
	private String fowardingWs;
	
//	@Value("${apigateway.import.ws}")
//	private String importSimWs;

	@Value("${apigateway.subcribers.update.ws}")
	private String updateOffertWs;
	
	@Value("${apigateway.sendSms.ws}")
	private String sendSmsWs;
	
	@Value("${apigateway.subcribers.apnchange}")
	private String apnChangeWs;

	public SelfConsumption getConsumption(String cellphoneNumber) {
		logger.info("Haciendo request de apigateway al metodo selfconsumpion para obtener consumo del número celular "
				+ cellphoneNumber);
		SelfConsumption consumption = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			String url = apiGatewayUrl + selfconsumpionWs + cellphoneNumber + SLASH + BUCKET_TEXT + QUESTION_MARK
					+ BE_ID + EQUAL + beId;
//			String response0 = restTemplate.getForObject(url, String.class);
//			System.out.println("RESPONSE: " +  response0);
			ResponseEntity<SelfConsumption> response = restTemplate.getForEntity(url, SelfConsumption.class);
			int status = response.getStatusCodeValue();
			if (status == 200) {
				consumption = response.getBody();
			}
		} catch (Exception e) {
			logger.error("Se ha generado un error al consultar el balance");
			logger.error(e.getMessage());
		}
		return consumption;
	}

	public String getOfferId(String cellphoneNumber) {
		logger.info("Haciendo request de apigateway al metodo profile para obtener oferta primaria del número celular "
				+ cellphoneNumber);
		String offerId = "";
		try {
			RestTemplate restTemplate = new RestTemplate();
			String url = apiGatewayUrl + subscribers + cellphoneNumber + SLASH + profileWs;
			final String jsonResponse = restTemplate.getForObject(url, String.class);
			JSONObject objectJson = new JSONObject(jsonResponse);
			JSONObject responseSubscriberJson = objectJson.getJSONObject("responseSubscriber");
			objectJson = new JSONObject(responseSubscriberJson.toString());
			JSONObject primaryOfferingJson = objectJson.getJSONObject("primaryOffering");
			offerId = primaryOfferingJson.getString("offeringId");
		} catch (Exception e) {
			logger.error("Se ha generado un error al consultar el plan");
			logger.error(e.getMessage());
		}
		return offerId;
	}

	public Boolean isTurboredMsisdn(String cellphoneNumber) {
		logger.info("Haciendo request de apigateway al metodo profile para el número celular " + cellphoneNumber);
		Boolean belongsToTurbored = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			String url = apiGatewayUrl + subscribers + cellphoneNumber + SLASH + profileWs;
			final String jsonResponse = restTemplate.getForObject(url, String.class);
			JSONObject objectJson = new JSONObject(jsonResponse);
			JSONObject responseSubscriberJson = objectJson.getJSONObject("responseSubscriber");
			if (responseSubscriberJson != null) {
				belongsToTurbored = true;
				logger.info(cellphoneNumber + " pertenece a la red de turbored");
			}
		} catch (HttpStatusCodeException ex) {
			// http status code
			logger.error(ex.getStatusCode().toString());
			if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
				// get response body
				logger.error(ex.getResponseBodyAsString());
				String jsonResponse = ex.getResponseBodyAsString();
				JSONObject objectJson = new JSONObject(jsonResponse);
				String errorCode = objectJson.getString("errorCode");
				if (errorCode.equals(SUSCRIBER_DOESNT_EXIS)) {
					logger.error(cellphoneNumber + " no pertenece a la red de turbored");
					belongsToTurbored = false;
				}
			}
		} catch (Exception e) {
			logger.error("Se ha generado un error al consultar el plan");
			logger.error(e.getMessage());
		}
		return belongsToTurbored;
	}

	public CoverageResponse validateServiceability(String coordinates) throws Exception {
		logger.info("Haciendo request de apigateway al metodo serviciability para validar covertura de " + coordinates);
		CoverageResponse coverageResponse = new CoverageResponse();
		String result = null;
		String description = null;
		RestTemplate restTemplate = new RestTemplate();
		String url = apiGatewayUrl + serviceabilityWs + coordinates;
		final String jsonResponse = restTemplate.getForObject(url, String.class);
		JSONObject objectJson = new JSONObject(jsonResponse);
		result = objectJson.getString("result");
		coverageResponse.setResult(result);
		if (objectJson.has("description")) { 
			description = objectJson.getString("description");
		}
		coverageResponse.setDescription(description);
		logger.info("Response result: " + result);
		logger.info("Response desciption: " + description);
		return coverageResponse;
	}

	public String getDeviceInformation(String imei) throws Exception {
		logger.info("Haciendo request de apigateway para consultar número celular de imei " + imei);
		String msisdn = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			String url = apiGatewayUrl + subscribers + deviceInformationWs + imei;
			SearchResponse searchResponse = null;
			ResponseEntity<SearchResponse> response = restTemplate.getForEntity(url, SearchResponse.class);
			int status = response.getStatusCodeValue();
			if (status == 200) {
				searchResponse = response.getBody();
				if (searchResponse != null) {
					msisdn = searchResponse.getMsisdn();
				}
				logger.info("Response msisdn: " + msisdn);
			}
		} catch (Exception e) {
			logger.error("Se ha generado un error al consultar la informacion del dispositivo por imei");
			logger.error(e.getMessage());
		}
		return msisdn;
	}

	public String checkImei(String imei) throws Exception {
		logger.info("Haciendo request de apigateway para consultar estatus de imei " + imei);
		String homologated = null;
		try {
		RestTemplate restTemplate = new RestTemplate();
		String url = apiGatewayUrl + imeisWs + imei + SLASH + imeiStatusWs + QUESTION_MARK + VERSION + EQUAL
				+ imeiStatusVersionParam;
		ImeiStatusResponse imeiStatusResponse = null;
		ResponseEntity<ImeiStatusResponse> response = restTemplate.getForEntity(url, ImeiStatusResponse.class);
		logger.info("Respuesta check imei " + response);
		int status = response.getStatusCodeValue();
		if (status == 200) {
			imeiStatusResponse = response.getBody();
			if (imeiStatusResponse != null) {
				homologated = imeiStatusResponse.getImei().getHomologated();
			}
			logger.info("Response homologated: " + homologated);
		}
		}catch (HttpClientErrorException h) {

			homologated = NOT_FOUND;

	   }
		return homologated;
	}

	public AltanActionResponse activateMsisdn(String cellphoneNumber, SubscribersActivateRequest subscriberActivateRequest)
			throws Exception {
		logger.info("Haciendo request de apigateway metodo activate para  msisdn " + cellphoneNumber + " offerid "
				+ subscriberActivateRequest.getOfferingId());
		AltanActionResponse altanActionResponse = new AltanActionResponse();
		String jsonResponse ="";
		String responseStatus = "";
		String url = null;
		String altanOrderId = null;
		ResponseEntity<SubscribersActivateResponse> response = null;
		HttpEntity<SubscribersActivateRequest> request = null;
		SubscribersActivateResponse subscribersActivateResponse = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			url = apiGatewayUrl + subscribers + cellphoneNumber + SLASH + activateSimWs;
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			request = new HttpEntity<>(subscriberActivateRequest, headers);
			response = restTemplate.exchange(url, HttpMethod.POST, request,
					SubscribersActivateResponse.class);
			int status = response.getStatusCodeValue();
			if (status == 200) {
				responseStatus = ResponseCodeConstants.SUCCES_STATUS;
				subscribersActivateResponse = response.getBody();
				logger.info("Response order: " + subscribersActivateResponse.getOrder().getId());
				altanOrderId = subscribersActivateResponse.getOrder().getId();
			} else {
				responseStatus = ResponseCodeConstants.FAILED_STATUS;
				logger.error("Error " + status);
			}
		} catch (HttpStatusCodeException ex) {
			// http status code
			responseStatus = ResponseCodeConstants.FAILED_STATUS;
			logger.error(ex.getStatusCode().toString());
			if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
				// get response body
				logger.error(ex.getResponseBodyAsString());
				jsonResponse = ex.getResponseBodyAsString();
				JSONObject objectJson = new JSONObject(jsonResponse);
				String errorCode = objectJson.getString("errorCode");
				String errorCodeDescription = objectJson.getString("description");
				if (errorCode.equals(ERROR_CODE_400) && errorCodeDescription.equals(ERROR_CODE_400_DESCRIPTION)) {
					logger.error(cellphoneNumber + " ya tiene una oferta asignada- " + ex.getMessage());
					throw new TrException("Este sim ya ha tiene una oferta primaria asignada.");
				}
			}
		}
		altanActionResponse = createAltanActionResponse(subscriberActivateRequest, subscribersActivateResponse, responseStatus, url, altanOrderId);
		return altanActionResponse;
	}

	public AltanActionResponse doBarring(String cellphoneNumber) {
		logger.info("Haciendo request de apigateway metodo barring al número celular  " + cellphoneNumber);
		return doSuscriberOperation(cellphoneNumber, barringWs);
	}

	public AltanActionResponse doUnbarring(String cellphoneNumber) {
		logger.info("Haciendo request de apigateway metodo unbarring al número celular  " + cellphoneNumber);
		return doSuscriberOperation(cellphoneNumber, unbarringWs);
	}

	public AltanActionResponse doSuspend(String cellphoneNumber) {
		logger.info("Haciendo request de apigateway metodo suspend al número celular  " + cellphoneNumber);
		return doSuscriberOperation(cellphoneNumber, suspendWs);
	}

	public AltanActionResponse doResume(String cellphoneNumber) {
		logger.info("Haciendo request de apigateway metodo resume al número celular  " + cellphoneNumber);
		return doSuscriberOperation(cellphoneNumber, resumeWs);
	}

	public AltanActionResponse doSuscriberOperation(String cellphoneNumber, String operation) {
		AltanActionResponse altanActionResponse = new AltanActionResponse();
		SuscribersGeneralResponse  suscribersGeneralResponse = null;
		String jsonResponse ="";
		String responseStatus = "";
		String url = null;
		String altanOrderId = null;
		ResponseEntity<SuscribersGeneralResponse> response = null;
		HttpEntity<SubscribersActivateRequest> request = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			url = apiGatewayUrl + subscribers + cellphoneNumber + SLASH + operation;
			logger.info("url: " + url);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			request = new HttpEntity<>(headers);
			response = restTemplate.exchange(url, HttpMethod.POST, request,
					SuscribersGeneralResponse.class);
			int status = response.getStatusCodeValue();
			if (status == 200) {
				logger.info("Response: " + response.getBody().getEffectiveDate());
				responseStatus = ResponseCodeConstants.SUCCES_STATUS;
				suscribersGeneralResponse  = response.getBody();
				altanOrderId = suscribersGeneralResponse.getOrder().getId();
			} else {
				logger.error("Error " + status);
				responseStatus = ResponseCodeConstants.FAILED_STATUS;
			}
		} catch (HttpStatusCodeException ex) {
			// http status code
			responseStatus = ResponseCodeConstants.FAILED_STATUS;
			logger.error(ex.getStatusCode().toString());
			if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
				// get response body
				logger.error(ex.getResponseBodyAsString());
				jsonResponse = ex.getResponseBodyAsString();
				JSONObject objectJson = new JSONObject(jsonResponse);
				String errorCode = objectJson.getString("errorCode");
				String errorCodeDescription = objectJson.getString("description");
				logger.error(errorCode + " - " + errorCodeDescription);
			}
		}
		
		altanActionResponse = createAltanActionResponse(request, suscribersGeneralResponse, responseStatus, url, altanOrderId);
		return altanActionResponse;
	}
	
	public ProfileResponse getOfferAndStatus(String cellphoneNumber) {
		logger.info(
				"Haciendo request de apigateway par obtener oferta y estatus del número celular " + cellphoneNumber);
		ProfileResponse profileResponse = null;
		String offerId = "";
		String status = "";
		try {
			RestTemplate restTemplate = new RestTemplate();
			String url = apiGatewayUrl + subscribers + cellphoneNumber + SLASH + profileWs;
			final String jsonResponse = restTemplate.getForObject(url, String.class);
			JSONObject objectJson = new JSONObject(jsonResponse);
			JSONObject responseSubscriberJson = objectJson.getJSONObject("responseSubscriber");
			objectJson = new JSONObject(responseSubscriberJson.toString());
			JSONObject primaryOfferingJson = objectJson.getJSONObject("primaryOffering");
			offerId = primaryOfferingJson.getString("offeringId");
			JSONObject statusJson = objectJson.getJSONObject("status");
			status = statusJson.getString("subStatus");
			profileResponse = new ProfileResponse();
			profileResponse.setCellphoneNumber(cellphoneNumber);
			profileResponse.setPrimaryOfferingId(offerId);
			profileResponse.setStatus(status);
		} catch (Exception e) {
			logger.error("Se ha generado un error al consultar  la oferta y el esatus del númeron celular"
					+ cellphoneNumber);
			logger.error(e.getMessage());
		}
		return profileResponse;
	}
	
	public AltanActionResponse replaceOffer(String cellphoneNumber, UpdateOfferRequest updateOfferRequest) {
		logger.info("Haciendo request de apigateway metodo update(Cambiar oferta) del número celular " + cellphoneNumber);
		AltanActionResponse altanActionResponse = new AltanActionResponse();
		String jsonResponse ="";
		String responseStatus = null;
		String url = null;
		String altanOrderId = null;
		UpdateOfferResponse updateOfferResponse = null;
		ResponseEntity<UpdateOfferResponse> response = null;
		try {
			url = apiGatewayUrl + updateOffertWs + cellphoneNumber;
			logger.info("url: " + url);
			
			RestTemplate patchRestTemplate = new RestTemplate();

			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setConnectTimeout(10000);
			requestFactory.setReadTimeout(10000);
			patchRestTemplate.setRequestFactory(requestFactory);
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<UpdateOfferRequest> request = new HttpEntity<>(updateOfferRequest, headers);
			response = patchRestTemplate.exchange(url, HttpMethod.PATCH, request, UpdateOfferResponse.class);
			jsonResponse = response.toString();
			int status = response.getStatusCodeValue();
			if (status == 200) {
				logger.info("Response: " + response.toString());
				updateOfferResponse = response.getBody();
				altanOrderId = updateOfferResponse.getOrder().getId();
				responseStatus = ResponseCodeConstants.SUCCES_STATUS;
			} else {
				responseStatus = ResponseCodeConstants.FAILED_STATUS;
				logger.error("Error " + status);
			}
			altanActionResponse = createAltanActionResponse(updateOfferRequest, updateOfferResponse, responseStatus, url, altanOrderId);
		} catch (HttpStatusCodeException ex) {
			responseStatus = ResponseCodeConstants.FAILED_STATUS;
			// http status code
			logger.error(ex.getStatusCode().toString());
			if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
				// get response body
				logger.error(ex.getResponseBodyAsString());
				jsonResponse = ex.getResponseBodyAsString();
				JSONObject objectJson = new JSONObject(jsonResponse);
				String errorCode = objectJson.getString("errorCode");
				String errorCodeDescription = objectJson.getString("description");
				logger.error(errorCode + " - " + errorCodeDescription);
			}
			altanActionResponse = createAltanActionResponse(updateOfferRequest, jsonResponse, responseStatus, url, altanOrderId);
		}
		return altanActionResponse;
	}
	
	public PurchaseResponse purchase(PurchaseRequest purchaseRequest)
			 {
		logger.info("Haciendo request de apigateway metodo purchase para  msisdn " + purchaseRequest.getMsisdn() + " offerid "
				+ purchaseRequest.getOfferings().get(0));
		PurchaseResponse purchaseResponse = null;
		String jsonResponse ="";
		String responseStatus = "";
		String url = null;
		String altanOrderId = null;
		ResponseEntity<PurchaseResponse> response = null;
		HttpEntity<PurchaseRequest> request = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			url = cameldemoApiUrl + expressRechargePurchaseWs;
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			request = new HttpEntity<>(purchaseRequest, headers);
			response = restTemplate.exchange(url, HttpMethod.POST, request,
					PurchaseResponse.class);
			int status = response.getStatusCodeValue();
			if (status == 200) {
				responseStatus = ResponseCodeConstants.SUCCES_STATUS;
				purchaseResponse = response.getBody();
				if(purchaseResponse.getResponseCode() == 0) {
					logger.info("Response order: " + purchaseResponse.getOrder().getId());
					altanOrderId = purchaseResponse.getOrder().getId();
				} else {
					logger.error("La recarga no pudo ser realizada, response code " + purchaseResponse.getResponseCode() + ". response description: " + purchaseResponse.getReponse_description());
				}
			} else {
				responseStatus = ResponseCodeConstants.FAILED_STATUS;
				logger.error("Error " + status);
			}
		} catch (HttpStatusCodeException ex) {
			// http status code
			responseStatus = ResponseCodeConstants.FAILED_STATUS;
			logger.error(ex.getStatusCode().toString());
			if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
				// get response body
				logger.error(ex.getResponseBodyAsString());
				jsonResponse = ex.getResponseBodyAsString();
				JSONObject objectJson = new JSONObject(jsonResponse);
				String errorCode = objectJson.getString("errorCode");
				String errorCodeDescription = objectJson.getString("description");
				if (errorCode.equals(ERROR_CODE_400) && errorCodeDescription.equals(ERROR_CODE_400_DESCRIPTION)) {
					logger.error(purchaseRequest.getMsisdn() + " Error al realizar la recarga " + ex.getMessage());
//					throw new TrException("Este sim ya ha tiene una oferta primaria asignada.");
				}
			}
		}
		AltanActionResponse altanActionResponse = createAltanActionResponse(request, purchaseResponse, responseStatus, url, altanOrderId);
		purchaseResponse.setAltanActionResponse(altanActionResponse);
		return purchaseResponse;
	}

	private AltanActionResponse createAltanActionResponse(Object requestObject, Object responseObject, String status, String url, String altanOrderId) {
		logger.info("Generando AltanActionResponse ");
		AltanActionResponse altanActionResponse = new AltanActionResponse();
		altanActionResponse.setStatus(status);
		altanActionResponse.setUrl(url);
		altanActionResponse.setAltanOrderId(altanOrderId);
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			if (requestObject != null) {
				String jsonRequest;
				jsonRequest = ow.writeValueAsString(requestObject);
				altanActionResponse.setJsonRequest(jsonRequest);
			}
			if (responseObject != null) {
				String jsonResponse = ow.writeValueAsString(responseObject);
//				String jsonResponse = responseObject.toString();
				altanActionResponse.setJsonResponse(jsonResponse);
			}
		} catch (JsonProcessingException e) {
			logger.error("No se pudo generar AltanActionResponse " ) ;
		}

		return altanActionResponse;
	}
	
	public ProfileDataResponse getProfile(String cellphoneNumber) {
		logger.info("Haciendo request de apigateway al metodo profile para obtener oferta perfil del número celular "
				+ cellphoneNumber);
		ProfileDataResponse profileDataResponse = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			String url = apiGatewayUrl + subscribers + cellphoneNumber + SLASH + profileWs;
			String jsonResponse = restTemplate.getForObject(url, String.class);
			JSONObject objectJson = new JSONObject(jsonResponse);
			JSONObject responseSubscriberJson = objectJson.getJSONObject("responseSubscriber");
			Gson gson = new Gson();
			profileDataResponse = gson.fromJson(responseSubscriberJson.toString(), ProfileDataResponse.class);
		} catch (Exception e) {
			logger.error("Se ha generado un error al consultar el perfil del número celular " + cellphoneNumber);
			logger.error(e.getMessage());
		}
		return profileDataResponse;
	}
	
	public SubscriberCoordinates getSubscriberCoordinates(String cellphoneNumber) {
		logger.info("Haciendo request de apigateway al metodo subscriberCoordinate para obtener oferta las coordenadas del número celular "
				+ cellphoneNumber);
		SubscriberCoordinates subscriberCoordinates = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			String url = apiGatewayUrl + subscribers + cellphoneNumber + SLASH + subscriberCoordinateWs;
			ResponseEntity<SubscriberCoordinates> response = restTemplate.getForEntity(url, SubscriberCoordinates.class);
			int status = response.getStatusCodeValue();
			if (status == 200) {
				subscriberCoordinates = response.getBody();
			} else {
				logger.error("Error " + status);
			}			
		} catch (Exception e) {
			logger.error("Se ha generado un error al consultar las coordenadas(subscriberCoordinate) del número celular " + cellphoneNumber);
			logger.error(e.getMessage());
		}
		return subscriberCoordinates;
	}
	
	public ManagementServiceResponse doFoward(ManagementServiceRequest managementServiceRequest) {
		logger.info("Haciendo foward  del numero virtual  " + managementServiceRequest.getMsisdn_callForwarding());
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		ManagementServiceResponse managementServiceResponse = null;
		String json;
		try {
			json = ow.writeValueAsString(managementServiceRequest);
			logger.info("json request: " + json);
			RestTemplate restTemplate = new RestTemplate();
			String url = apiGatewayUrl + fowardingWs;
			logger.info("URL " + url);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			HttpEntity<ManagementServiceRequest> request = new HttpEntity<>(managementServiceRequest, headers);
			ResponseEntity<ManagementServiceResponse> response = null;
			response = restTemplate.exchange(url, HttpMethod.POST, request, ManagementServiceResponse.class);
			int responseStatus = response.getStatusCodeValue();
			logger.info("responseStatus de la peticion " + responseStatus);
			if (responseStatus == 200) {
				logger.info("Response: " + response.toString());
				managementServiceResponse = response.getBody();
			} else {
				logger.error("Error " + responseStatus);
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return managementServiceResponse;
	}
	
	public ApiGatewayGeneralResponse sendSms(SendSmsRequest sendSmsRequest) {
		logger.info("Eviando sms al numero " + sendSmsRequest.getNumber());
		ApiGatewayGeneralResponse apiGatewayGeneralResponse = null;
		RestTemplate restTemplate = new RestTemplate();
		String url = apiGatewayUrl + sendSmsWs;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<SendSmsRequest> request = new HttpEntity<>( sendSmsRequest, headers);
		ResponseEntity<ApiGatewayGeneralResponse> response = null;
		response = restTemplate.exchange(url, HttpMethod.POST, request,
				ApiGatewayGeneralResponse.class);
		logger.info("response" + response.getBody());
		int responseStatus = response.getStatusCodeValue();
		if (responseStatus == 200) {
			apiGatewayGeneralResponse = response.getBody();
			logger.info("Mensaje enviado exitosamente al numero " + sendSmsRequest.getNumber());
		} else {
			logger.error("Error al enviar mensaje al numero" + sendSmsRequest.getNumber() + " responseStatus  " + responseStatus);
		}
		return apiGatewayGeneralResponse;
	}
	
	public GeneralResponse apnChange(String phone) throws Exception {
		logger.info("apn change " + phone);
		GeneralResponse generalResponse = new GeneralResponse();
		RestTemplate restTemplate = new RestTemplate();
		String url = apiGatewayUrl + subscribers + phone + SLASH + apnChangeWs;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<String> request = new HttpEntity<>( headers);
		ResponseEntity<ApiGatewayGeneralResponse> response = null;
		try {
		response = restTemplate.exchange(url, HttpMethod.POST, request,
				ApiGatewayGeneralResponse.class);
		logger.info("response" + response.getBody());
		int responseStatus = response.getStatusCodeValue();
		if (responseStatus == 200) {
			generalResponse.setMessage("En breve recibirás un SMS con las configuraciones necesarias para que puedas navegar con tu equipo y disfrutes de la mejor conexión"); // TODO
			
			generalResponse.setCode(0);
			logger.info("operacion exitosa  " + phone);
		} else {
//			generalResponse.setMessage("SUCCESS");
//			generalResponse.setCode(0);
			logger.error("Error al enviar mensaje al numero" + phone + " responseStatus  " + responseStatus);
		}
	} catch (HttpStatusCodeException ex) {
		String jsonResponse ="";
		logger.error(ex.getStatusCode().toString());
		if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
			// get response body
			logger.error(ex.getResponseBodyAsString());
			jsonResponse = ex.getResponseBodyAsString();
			JSONObject objectJson = new JSONObject(jsonResponse);
			String errorCode = objectJson.getString("errorCode");
			String errorCodeDescription = objectJson.getString("description");
			if (errorCode.equals(ERROR_CODE_400) && errorCodeDescription.equals(ERROR_CODE_400_APN_CHANGE_IN_PROCESS)) {
				throw new TrException("Ya existe una actualización de APN enviada previamente, es necesario esperar 24 horas para volver a intentar.");
			} else {
				throw ex;
			}
		}
	}
		return generalResponse;
	}
	
}
