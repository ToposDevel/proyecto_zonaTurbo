package com.topostechnology.rest.client;

import java.util.Collections;
import java.util.Date;

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

import com.topostechnology.constant.ResponseCodeConstants;
import com.topostechnology.exception.TrException;
import com.topostechnology.exception.TurboApiException;
import com.topostechnology.rest.client.request.CoordinatesRequest;
import com.topostechnology.rest.client.request.PaymentLinkNotificationRequest;
import com.topostechnology.rest.client.request.SavePortingRequest;
import com.topostechnology.rest.client.request.TurboOfficeExtraRechargeRequest;
import com.topostechnology.rest.client.request.TurboOfficePaymentNotificationRequest;
import com.topostechnology.rest.client.request.TurboOfficePlanResponse;
import com.topostechnology.rest.client.request.TurboOfficeRegisterRequest;
import com.topostechnology.rest.client.request.UpdateStatusPortingRequest;
import com.topostechnology.rest.client.request.UserValidationCodeRequest;
import com.topostechnology.rest.client.response.CoordinatesResponse;
import com.topostechnology.rest.client.response.GeneralResponse;
import com.topostechnology.rest.client.response.PaymentLinkResponse;
import com.topostechnology.rest.client.response.PortingResponse;
import com.topostechnology.rest.client.response.TurboOfficeCallDetailResponse;
import com.topostechnology.rest.client.response.TurboOfficeUserInfoResponse;
import com.topostechnology.rest.client.response.TurboOfficeUserPlanDetailResponse;
import com.topostechnology.rest.client.response.TurboOfficeUsersResponse;

@Service
public class TurboApiClient extends BaseApiClient {
	
	@Value("${turboapi.url}")
	private String turboApiUrl;
	
	@Value("${turboapi.getcoordinates}")
	private String coordinatesUrl;
	
	@Value("${turboapi.porting.save}")
	private String savePortingUrl;
	
	@Value("${turboapi.porting.getStatus}")
	private String getPortingStatusurl;
	
	@Value("${turboapi.porting.updateStatus}")
	private String updatePortingStatusurl;
	
	@Value("${turboapi.user}")
	private String turboApiUser;
	
	@Value("${turboapi.password}")
	private String turboApiPass;
	
	@Value("${turboapi.turboOffice.saveUser}")
	private String tOfficeSaveUserWs;
	
	@Value("${turboapi.turboOffice.getTurboOfficeActivePlans}")
	private String getTurboOfficeActivePlansWs;
	
	@Value("${turboapi.turboOffice.getTurboOfficeUserPlanDetail}")
	private String getTurboOfficeUserPlanDetailws;
	
	@Value("${turboapi.turboOffice.saveExtraRechargeRequest}")
	private String extraRechargeRequestWs;
	
	@Value("${turboapi.turboOffice.processPeriodPayments}")
	private String processPeriodPaymentsWs;
	
	@Value("${turboapi.turboOffice.users}")
	private String getTurboOfficeUsersWs;
	
	@Value("${turboapi.turboOffice.getUserInfo}")
	private String getUserInfoWs;
	
	@Value("${turboapi.turboOffice.deleteUser}")
	private String deleteUserWs;
	
	@Value("${turboapi.turboOffice.getCallDetails}")
	private String callDetailsWs;
	
	@Value("${turboapi.turboOffice.unfreezeNumbers}")
	private String unfreezeNumbersWs;
	
	@Value("${turboapi.turboOffice.generatePaymentLink}")
	private String generatePaymentLinkWs;
	
	@Value("${turboapi.turboOffice.sendUserCodeBySms}")
	private String sendUserCodeBySmsWs;
	
	@Value("${turboapi.turboOffice.validateUserCode}")
	private String validateUserCodeWs;
	
	
	private static final Logger logger = LoggerFactory.getLogger(TurboApiClient.class);

	public CoordinatesResponse getCoordinates(String address, String zipcode) throws Exception {
		logger.info("Obteniendo coordenadas para " + address + "" + zipcode);
		CoordinatesResponse coordinates = null;
		String url = turboApiUrl + coordinatesUrl;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setBasicAuth(turboApiUser, turboApiPass);
		CoordinatesRequest coordinatesRequest = new CoordinatesRequest();
		coordinatesRequest.setAddress(address);
		coordinatesRequest.setZipcode(zipcode);
		RestTemplate restTemplate = this.getRestTemplateJsonGET();
		HttpEntity<CoordinatesRequest> request = new HttpEntity<>(coordinatesRequest, headers);
		ResponseEntity<CoordinatesResponse> response = restTemplate.exchange(url, HttpMethod.GET, request,
				CoordinatesResponse.class);
		int status = response.getStatusCodeValue();
		if (status == 200) {
			coordinates = response.getBody();
			logger.info("Direccion encontrada: " + coordinates.getFormattedAddress());
			logger.info(coordinates.getMessage());
		}
		return coordinates;
	}
	
//	public void sendBalanceAlert() throws Exception { // TODO QUITAR SOLO DE PRUEBA
//		logger.info("Enviando notificacion al usuario con número de celular ");
//			String url = "https://10.1.10.16:7342/turboApi/sendBalanceAlert";// TODO 
//			HttpHeaders headers = new HttpHeaders();
//			headers.setContentType(MediaType.APPLICATION_JSON);
//			headers.setBasicAuth(turboApiUser, "turboApiPass");
//			BalanceAlertRequest balanceAlertRequest = new BalanceAlertRequest();
//			balanceAlertRequest.setCellphoneNumber("5586410030");
//			balanceAlertRequest.setPercentageConsumed("80");
//			HttpEntity<BalanceAlertRequest> request = new HttpEntity<>(balanceAlertRequest, headers);
//			RestTemplate restTemplate = this.getRestTemplate();
//			HttpEntity<String> responseStr = restTemplate.exchange(url,  HttpMethod.POST, request, String.class);
//	}
	
	public void savePorting(SavePortingRequest savePortingRequest) throws Exception { // TODO QUITAR SOLO DE PRUEBA
		logger.info("Registrando datos(TurboApi) de portabilidad para el número " + savePortingRequest.getMsisdnPorted());
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBasicAuth(turboApiUser, turboApiPass);
			HttpEntity<SavePortingRequest> request = new HttpEntity<>(savePortingRequest, headers);
			RestTemplate restTemplate = this.getRestTemplate();
			String url = turboApiUrl + savePortingUrl;
			ResponseEntity<GeneralResponse> response = restTemplate.exchange(url, HttpMethod.POST, request,
					GeneralResponse.class);
			int status = response.getStatusCodeValue();
			logger.info("Response status " + status);
			if (status == 200) {
				GeneralResponse generalResponse = response.getBody();
				if(generalResponse.getCode() == ResponseCodeConstants.ERROR_CODE) {
					throw new TrException(generalResponse.getMessage());
				}
			}
	}
	
	public PortingResponse getPortingStatus(String cellphoneNumber) throws Exception {
		logger.info("Consultando estatus de portabilidad para el número "  + cellphoneNumber);
		String url = turboApiUrl + getPortingStatusurl + cellphoneNumber;
		HttpHeaders headers = new HttpHeaders();
		PortingResponse portingResponse = null;
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setBasicAuth(turboApiUser, turboApiPass);
		RestTemplate restTemplate = this.getRestTemplateJsonGET();
		HttpEntity<CoordinatesRequest> request = new HttpEntity<>( headers);
		ResponseEntity<PortingResponse> response = restTemplate.exchange(url, HttpMethod.GET, request,
				PortingResponse.class);
		int status = response.getStatusCodeValue();
		if (status == 200) {
			portingResponse = response.getBody();
		}
		return portingResponse;
	}
	
	public GeneralResponse updatePortingStatus(UpdateStatusPortingRequest portingRequest) throws Exception { 
		logger.info("Actualizando estatus de portabildiad para el numero  " + portingRequest.getCellphoneNumber() + " STATUS: " + portingRequest.getStatus());
		GeneralResponse generalResponse = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBasicAuth(turboApiUser, turboApiPass);
		HttpEntity<UpdateStatusPortingRequest> request = new HttpEntity<>(portingRequest, headers);
		RestTemplate restTemplate = this.getRestTemplate();
		String url = turboApiUrl + updatePortingStatusurl;
		ResponseEntity<GeneralResponse> response = restTemplate.exchange(url, HttpMethod.POST, request,
				GeneralResponse.class);
		int status = response.getStatusCodeValue();
		if (status == 200) {
			generalResponse = response.getBody();
			if(generalResponse.getCode() == ResponseCodeConstants.ERROR_CODE) {
				throw new TrException(generalResponse.getMessage());
			}
		}
		return generalResponse;
	}
	
	public GeneralResponse saveTurboOfficeUser(TurboOfficeRegisterRequest tOfficeRegisterRequest) throws Exception { 
		logger.info("Registrando turbo office user  " + tOfficeRegisterRequest.getCellphoneNumber() );
		GeneralResponse generalResponse = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBasicAuth(turboApiUser, turboApiPass);
		HttpEntity<TurboOfficeRegisterRequest> request = new HttpEntity<>(tOfficeRegisterRequest, headers);
		RestTemplate restTemplate = this.getRestTemplate();
		String url = turboApiUrl + tOfficeSaveUserWs;
		ResponseEntity<GeneralResponse> response = restTemplate.exchange(url, HttpMethod.POST, request,
				GeneralResponse.class);
		int status = response.getStatusCodeValue();
		if (status == 200) {
			generalResponse = response.getBody();
			if(generalResponse.getCode() == ResponseCodeConstants.ERROR_CODE) {
				throw new TrException(generalResponse.getMessage());
			}
		}
		return generalResponse;
	}
	
	public TurboOfficePlanResponse getTurboOfficeActivePlans() throws Exception {
		logger.info("Obteniendo planes activos de Turbo office ");
		TurboOfficePlanResponse turboOfficePlanResponse = null;
		String url = turboApiUrl + getTurboOfficeActivePlansWs;
		logger.info(url);
//		String url = "https://localhost:7342/turboApi/turboOffice/turboOfficeActivePlans";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setBasicAuth(turboApiUser, turboApiPass);
		RestTemplate restTemplate = this.getRestTemplateJsonGET();
		HttpEntity<TurboOfficePlanResponse> request = new HttpEntity<>(null, headers);
		ResponseEntity<TurboOfficePlanResponse> response = restTemplate.exchange(url, HttpMethod.GET, request,
				TurboOfficePlanResponse.class);
		int status = response.getStatusCodeValue();
		if (status == 200) {
			turboOfficePlanResponse = response.getBody();
		} else {
			logger.error("Error " + status);
		}
		return turboOfficePlanResponse;
	}
	
	public TurboOfficeUserPlanDetailResponse getTurboOfficeUserPlanDetail(String virtualNumber) throws Exception {
		logger.info("Obteniendo TurboOfficeUserPlanDetail para  " + virtualNumber);
		TurboOfficeUserPlanDetailResponse turboOfficeUserPlanDetailResponse = null;
		String url = turboApiUrl + getTurboOfficeUserPlanDetailws + virtualNumber;
		logger.info(url);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setBasicAuth(turboApiUser, turboApiPass);
		RestTemplate restTemplate = this.getRestTemplateJsonGET();
		HttpEntity<TurboOfficePlanResponse> request = new HttpEntity<>(null, headers);
		ResponseEntity<TurboOfficeUserPlanDetailResponse> response = restTemplate.exchange(url, HttpMethod.GET, request,
				TurboOfficeUserPlanDetailResponse.class);
		int status = response.getStatusCodeValue();
		if (status == 200) {
			turboOfficeUserPlanDetailResponse = response.getBody();
		} else {
			logger.error("Error " + status);
		}
		return turboOfficeUserPlanDetailResponse;
	}
	
	public GeneralResponse saveExtraRechargeRequest(TurboOfficeExtraRechargeRequest turboOfficeExtraRechargeRequest) throws Exception { 
		logger.info("Registrando solicitud de recarga adicional para  " + turboOfficeExtraRechargeRequest.getVirtualNumber() );
		GeneralResponse generalResponse = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBasicAuth(turboApiUser, turboApiPass);
		HttpEntity<TurboOfficeExtraRechargeRequest> request = new HttpEntity<>(turboOfficeExtraRechargeRequest, headers);
		RestTemplate restTemplate = this.getRestTemplate();
		String url = turboApiUrl + extraRechargeRequestWs;
		ResponseEntity<GeneralResponse> response = restTemplate.exchange(url, HttpMethod.POST, request,
				GeneralResponse.class);
		int status = response.getStatusCodeValue();
		if (status == 200) {
			generalResponse = response.getBody();
			if(generalResponse.getCode() == ResponseCodeConstants.ERROR_CODE) {
				throw new TrException(generalResponse.getMessage());
			}
		}
		return generalResponse;
	}
	
	public GeneralResponse periodicPaymentNotification(TurboOfficePaymentNotificationRequest turboOfficePaymentNotificationRequest) throws Exception { 
		logger.info("Registrando ágo periodico para  " + turboOfficePaymentNotificationRequest.getCellphoneNumber() );
		GeneralResponse generalResponse = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBasicAuth(turboApiUser, turboApiPass);
		HttpEntity<TurboOfficePaymentNotificationRequest> request = new HttpEntity<>(turboOfficePaymentNotificationRequest, headers);
		RestTemplate restTemplate = this.getRestTemplate();
		String url = turboApiUrl + extraRechargeRequestWs;
		ResponseEntity<GeneralResponse> response = restTemplate.exchange(url, HttpMethod.POST, request,
				GeneralResponse.class);
		int status = response.getStatusCodeValue();
		if (status == 200) {
			generalResponse = response.getBody();
			if(generalResponse.getCode() == ResponseCodeConstants.ERROR_CODE) {
				throw new TrException(generalResponse.getMessage());
			}
		}
		return generalResponse;
	}
	
	public GeneralResponse processPeriodPayments() throws Exception {
		logger.info("Procesando pago peridodico");
		String url = turboApiUrl + processPeriodPaymentsWs;
		HttpHeaders headers = new HttpHeaders();
		GeneralResponse generalResponse = null;
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setBasicAuth(turboApiUser, turboApiPass);
		RestTemplate restTemplate = this.getRestTemplateJsonGET();
		HttpEntity<CoordinatesRequest> request = new HttpEntity<>( headers);
		ResponseEntity<PortingResponse> response = restTemplate.exchange(url, HttpMethod.GET, request,
				PortingResponse.class);
		int status = response.getStatusCodeValue();
		if (status == 200) {
			generalResponse = response.getBody();
		}
		return generalResponse;
	}
	
	public TurboOfficeUsersResponse getTurboOfficeUsers() throws Exception {
		logger.info("Consultando usuarios turbo office");
		String url = turboApiUrl + getTurboOfficeUsersWs;
		HttpHeaders headers = new HttpHeaders();
		TurboOfficeUsersResponse turboOfficeUsersResponse = null;
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setBasicAuth(turboApiUser, turboApiPass);
		RestTemplate restTemplate = this.getRestTemplateJsonGET();
		HttpEntity<CoordinatesRequest> request = new HttpEntity<>( headers);
		ResponseEntity<TurboOfficeUsersResponse> response = restTemplate.exchange(url, HttpMethod.GET, request,
				TurboOfficeUsersResponse.class);
		int status = response.getStatusCodeValue();
		if (status == 200) {
			turboOfficeUsersResponse = response.getBody();
		}
		return turboOfficeUsersResponse;
	}
	
	public TurboOfficeUserInfoResponse getUserInfo(String virtualNumber) throws Exception { 
		logger.info("Consultando informacion de numero virtual " + virtualNumber );
		TurboOfficeUserInfoResponse turboOfficeUserInfoResponse = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBasicAuth(turboApiUser, turboApiPass);
		HttpEntity<String> request = new HttpEntity<>(virtualNumber, headers);
		RestTemplate restTemplate = this.getRestTemplate();
		String url = turboApiUrl + getUserInfoWs + virtualNumber;
		ResponseEntity<TurboOfficeUserInfoResponse> response = restTemplate.exchange(url, HttpMethod.POST, request,
				TurboOfficeUserInfoResponse.class);
		int status = response.getStatusCodeValue();
		if (status == 200) {
			turboOfficeUserInfoResponse = response.getBody();
			if(turboOfficeUserInfoResponse.getCode() == ResponseCodeConstants.ERROR_CODE) {
				throw new TrException(turboOfficeUserInfoResponse.getMessage());
			}
		}
		return turboOfficeUserInfoResponse;
	}
	
	public TurboOfficeCallDetailResponse getCallDetailsWs(Long turboOfficeUserPlanDetailId) throws Exception { 
		logger.info("Consultando detalle de llamadas " + turboOfficeUserPlanDetailId );
		TurboOfficeCallDetailResponse turboOfficeCallDetailResponse = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBasicAuth(turboApiUser, turboApiPass);
		HttpEntity<Long> request = new HttpEntity<>(turboOfficeUserPlanDetailId, headers);
		RestTemplate restTemplate = this.getRestTemplate();
		String url = turboApiUrl + callDetailsWs + turboOfficeUserPlanDetailId;
		ResponseEntity<TurboOfficeCallDetailResponse> response = restTemplate.exchange(url, HttpMethod.POST, request,
				TurboOfficeCallDetailResponse.class);
		int status = response.getStatusCodeValue();
		if (status == 200) {
			turboOfficeCallDetailResponse = response.getBody();
			if(turboOfficeCallDetailResponse.getCode() == ResponseCodeConstants.ERROR_CODE) {
				throw new TrException(turboOfficeCallDetailResponse.getMessage());
			}
		}
		return turboOfficeCallDetailResponse;
	}

	
	public GeneralResponse deleteUser(String virtualNumber) throws TurboApiException {
		logger.info("Eliminando registro " + virtualNumber );
		GeneralResponse generalResponse = null;
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBasicAuth(turboApiUser, turboApiPass);
			HttpEntity<String> request = new HttpEntity<>(virtualNumber, headers);
			RestTemplate restTemplate;
			restTemplate = this.getRestTemplate();
			String url = turboApiUrl + deleteUserWs + virtualNumber;
			ResponseEntity<GeneralResponse> response = restTemplate.exchange(url, HttpMethod.POST, request,
					GeneralResponse.class);
			int status = response.getStatusCodeValue();
			if (status == 200) {
				generalResponse = response.getBody();
			}
		} catch (Exception e) {
			logger.error("El usuario no pudo ser eliminado en turbo office " + virtualNumber + " " + generalResponse.getMessage());
		}
		return generalResponse;
	}
	
	public GeneralResponse unfreezeNumbers(Date date) throws Exception { 
		logger.info("Descongelando numeros congelados antes de    " + date);
		GeneralResponse generalResponse = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBasicAuth(turboApiUser, turboApiPass);
		HttpEntity<Date> request = new HttpEntity<>(date, headers);
		RestTemplate restTemplate = this.getRestTemplate();
		String url = turboApiUrl + unfreezeNumbersWs + date;
		ResponseEntity<GeneralResponse> response = restTemplate.exchange(url, HttpMethod.GET, request,
				GeneralResponse.class);
		int status = response.getStatusCodeValue();
		if (status == 200) {
			generalResponse = response.getBody();
			if(generalResponse.getCode() == ResponseCodeConstants.ERROR_CODE) {
				throw new TrException(generalResponse.getMessage());
			}
		}
		return generalResponse;
	}
	
	public PaymentLinkResponse generatePaymentLink(String  virtualNumber) throws Exception { 
		logger.info("Generando link de pago para  " + virtualNumber);
		PaymentLinkResponse paymentLinkResponse = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBasicAuth(turboApiUser, turboApiPass);
		HttpEntity<String> request = new HttpEntity<>(virtualNumber, headers);
		RestTemplate restTemplate = this.getRestTemplate();
		String url = turboApiUrl + generatePaymentLinkWs + virtualNumber;
		ResponseEntity<PaymentLinkResponse> response = restTemplate.exchange(url, HttpMethod.GET, request,
				PaymentLinkResponse.class);
		int status = response.getStatusCodeValue();
		if (status == 200) {
			paymentLinkResponse = response.getBody();
			if(paymentLinkResponse.getCode() == ResponseCodeConstants.ERROR_CODE) {
				throw new TrException(paymentLinkResponse.getMessage());
			}
		}
		return paymentLinkResponse;
	}
	
	public GeneralResponse sendUserCodeBySmsWs(UserValidationCodeRequest userValidationCodeRequest) throws TurboApiException {
		logger.info("Enviando codigo a " + userValidationCodeRequest.getCellphoneNumber() );
		GeneralResponse generalResponse = null;
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBasicAuth(turboApiUser, turboApiPass);
			HttpEntity<UserValidationCodeRequest> request = new HttpEntity<>(userValidationCodeRequest, headers);
			RestTemplate restTemplate;
			restTemplate = this.getRestTemplate();
			String url = turboApiUrl + sendUserCodeBySmsWs ;
			ResponseEntity<GeneralResponse> response = restTemplate.exchange(url, HttpMethod.POST, request,
					GeneralResponse.class);
			int status = response.getStatusCodeValue();
			if (status == 200) {
				generalResponse = response.getBody();
			}
		} catch (Exception e) {
			logger.error("No se pudo enviar el codigo por mensaje a " + userValidationCodeRequest.getCellphoneNumber() + " " + generalResponse.getMessage());
		}
		return generalResponse;
	}	

	public GeneralResponse validateUserCode(UserValidationCodeRequest userValidationCodeRequest) throws TurboApiException {
		logger.info("Validando codigo de " + userValidationCodeRequest.getCellphoneNumber() );
		GeneralResponse generalResponse = null;
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBasicAuth(turboApiUser, turboApiPass);
			HttpEntity<UserValidationCodeRequest> request = new HttpEntity<>(userValidationCodeRequest, headers);
			RestTemplate restTemplate;
			restTemplate = this.getRestTemplate();
			String url = turboApiUrl + validateUserCodeWs ;
			ResponseEntity<GeneralResponse> response = restTemplate.exchange(url, HttpMethod.POST, request,
					GeneralResponse.class);
			int status = response.getStatusCodeValue();
			if (status == 200) {
				generalResponse = response.getBody();
			}
		} catch (Exception e) {
			logger.error("No se pudo validar el codigo de " + userValidationCodeRequest.getCellphoneNumber() + " " + generalResponse.getMessage());
		}
		return generalResponse;
	}
	
	public GeneralResponse linkPayment(PaymentLinkNotificationRequest paymentLinkNotificationRequest) throws Exception {
		logger.info("Probando link de pago para el numero  " + paymentLinkNotificationRequest.getCellphoneNumber() + " LINK: " + paymentLinkNotificationRequest.getPaymentLink());
		GeneralResponse generalResponse = null;

		try {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBasicAuth("zonaturbo_user01", "z0n@T4rb0$$2021!");
		HttpEntity<PaymentLinkNotificationRequest> request = new HttpEntity<>(paymentLinkNotificationRequest, headers);
		RestTemplate restTemplate = this.getRestTemplate();
		//String url = turboApiUrl + paymentLink;
		String url ="https://turboapp.turboredapp.com/turboApi-test/paymentLink/paymentNotification";
		ResponseEntity<GeneralResponse> response = restTemplate.exchange(url, HttpMethod.POST, request,
				GeneralResponse.class);
		int status = response.getStatusCodeValue();
		if (status == 200) {
			generalResponse = response.getBody();
			if(generalResponse.getCode() == ResponseCodeConstants.ERROR_CODE) {
				throw new TrException(generalResponse.getMessage());
			}
		}

		} catch (Exception e) {
			logger.error("Ocurrio un error");
		}
		return generalResponse;
	}
}
