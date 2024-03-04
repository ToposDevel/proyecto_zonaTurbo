package com.topostechnology.rest.client;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.topostechnology.rest.client.request.CitiConsultingOfferRequest;
import com.topostechnology.rest.client.request.OfferIdConektaAltanRequest;
import com.topostechnology.rest.client.request.OfferIdConektaAltanResponse;
import com.topostechnology.rest.client.response.CitiConsultingOffersResponse;
import com.topostechnology.rest.client.response.IccidInfoResponse;
import com.topostechnology.rest.client.response.OfertasReemplazoResponse;
import com.topostechnology.rest.client.response.OfferModalityResponse;
import com.topostechnology.rest.client.response.PerfilResponse;
import com.topostechnology.rest.client.response.SaldoResponse;

@Service
public class GategayApiXtraClient extends BaseApiClient {
	
	@Value("${cameldemo.api.url}")
	private String apiUrl;
	
	@Value("${apigateway.offerid.conekta.altan}")
	private String offerIdConektaAltanWs;
	
	@Value("${apigateway.citi.api.consulting.offer.ws}")
	private String citiConsultingOfferWs;
	
	@Value("${apigateway.getOfferProfile.ws}")
	private String geOfferProfileWs;
	
	@Value("${apigateway.getIiccidInfo.ws}")
	private String getIccidInfoWs;
	
	@Value("${apigateway.getReplacementOffers.ws}")
	private String getReplacementOffersWs;
	
	@Value("${apigateway.getOfferModality.ws}")
	private String getOfferModalityWs;
	
	@Value("${apigateway.getBalanceAndExpireDate.ws}")
	private String getBalanceAndExpireDateWs;
	
	private static final Logger logger = LoggerFactory.getLogger(GategayApiXtraClient.class);
	
	public OfferIdConektaAltanResponse getofferIdConektaAltan(String tipoOperacion, String conektaOfferId) {
		logger.info("Consultando offerId de altan para offerId de conekta " + conektaOfferId);
		OfferIdConektaAltanResponse offerIdConektaAltanResponse = null;
		String url = apiUrl + offerIdConektaAltanWs;
		logger.info("Url " + url);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		OfferIdConektaAltanRequest offerIdConektaAltanRequest = new OfferIdConektaAltanRequest();
		offerIdConektaAltanRequest.setOferta(conektaOfferId);
		offerIdConektaAltanRequest.setTipoOperacion(tipoOperacion);
		HttpEntity<OfferIdConektaAltanRequest> request = new HttpEntity<>(offerIdConektaAltanRequest, headers);
		try {
			RestTemplate restTemplate = this.getRestTemplateJsonGET();
			ResponseEntity<OfferIdConektaAltanResponse> response = restTemplate.exchange(url, HttpMethod.GET, request,
					OfferIdConektaAltanResponse.class);
			int status = response.getStatusCodeValue();
			if (status == 200) {
				offerIdConektaAltanResponse = response.getBody();
				logger.info("Oferta altan " + offerIdConektaAltanResponse.getOfertaAltan());
			}
		}catch(Exception e) {
			logger.error("Error al consultar offerId de altan para offerId de conekta " + conektaOfferId + " " + e.getMessage());
			e.printStackTrace();
		}
		return offerIdConektaAltanResponse;
	}
	
	public CitiConsultingOffersResponse getCitiConsultingOffer(CitiConsultingOfferRequest citiConsultingOfferRequest)
			throws Exception {
		logger.info("Consultando citi  ofertas validas para  recargar el número celular " + citiConsultingOfferRequest.getMsisdn());
		CitiConsultingOffersResponse citiConsultingOffersResponse = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			String url = apiUrl + citiConsultingOfferWs;
			logger.info("Url " + url);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<CitiConsultingOfferRequest> request = new HttpEntity<>(citiConsultingOfferRequest, headers);
			ResponseEntity<CitiConsultingOffersResponse> response = restTemplate.exchange(url,  HttpMethod.POST, request, CitiConsultingOffersResponse.class);
			int status = response.getStatusCodeValue();
			if (status == 200) {
				citiConsultingOffersResponse = response.getBody();
				logger.info("Response code: " + citiConsultingOffersResponse.getResponseCode());
			} else {
				logger.error("Error " + status);
			}

		} catch (HttpStatusCodeException ex) {
			logger.error(ex.getStatusCode().toString());
			throw ex;
		} 
		
		return citiConsultingOffersResponse;
	}
	
	public PerfilResponse getOfferSuperType(String cellphoneNumber) throws Exception {
		logger.info("Consultando perfil(Obtener superType) de oferta para el número celular " + cellphoneNumber);
		PerfilResponse perfilResponse = null;
//			String url = "http://104.245.125.234:10655/suscripciones/api/consultaperfil/" + cellphoneNumber; // TODO PASAR AL .properties
		String url = apiUrl + geOfferProfileWs +cellphoneNumber;
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			HttpEntity<String> request = new HttpEntity<>( headers);
			RestTemplate restTemplate = this.getRestTemplateJsonGET();
			ResponseEntity<PerfilResponse> response = restTemplate.exchange(url, HttpMethod.GET, request,
					PerfilResponse.class);
			int status = response.getStatusCodeValue();
			if (status == 200) {
				perfilResponse = response.getBody();
			}  else {
				logger.error("Error " + status);
			}
			return perfilResponse;
	} 
	
	public IccidInfoResponse getIccidInfo(String iccid) {
		logger.info("Consultando info del iccid  " + iccid);
		IccidInfoResponse iccidInfoResponse = null;
		try {
			String url = apiUrl + getIccidInfoWs + iccid;
			logger.info("Url " + url);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			HttpEntity<String> request = new HttpEntity<>( headers);
			RestTemplate restTemplate = this.getRestTemplateJsonGET();
			ResponseEntity<IccidInfoResponse> response = restTemplate.exchange(url, HttpMethod.GET, request,
					IccidInfoResponse.class);
			int status = response.getStatusCodeValue();
			logger.info("Response status: " + status);
			if (status == 200) {
				iccidInfoResponse = response.getBody();
			}  else {
				logger.error("Error " + status);
			}
		} catch(Exception e) {
			logger.error("Error al consultar informacion de iccid" + iccid );
			logger.error(e.getMessage());
		}
		return iccidInfoResponse;
	}
	
	public OfertasReemplazoResponse getReplacementOffers(String cellphoneNumber, String product, String offer) {
		logger.info("Consultando ofertas de reemplazo para el número celular  " + cellphoneNumber);
		OfertasReemplazoResponse ofertasReemplazoResponse = null;
		try {
			String url = apiUrl + getReplacementOffersWs + cellphoneNumber+"/"+product+"/"+offer;
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			HttpEntity<String> request = new HttpEntity<>( headers);
			RestTemplate restTemplate = this.getRestTemplateJsonGET();
			ResponseEntity<OfertasReemplazoResponse> response = restTemplate.exchange(url, HttpMethod.GET, request,
					OfertasReemplazoResponse.class);
			int status = response.getStatusCodeValue();
			if (status == 200) {
				ofertasReemplazoResponse = response.getBody();
			}  else {
				logger.error("Error ultar ofertas de remplazo para el número celular STATUS REPSONSE " + status);
			}
		} catch(Exception e) {
			logger.error("Error al consultar ofertas de remplazo para el número celular" + cellphoneNumber );
			logger.error(e.getMessage());
		}
		return ofertasReemplazoResponse;
	}
	
	/**
	 * Consulta modalidad de la oferta
	 * @param offerId
	 * @return
	 */
	public OfferModalityResponse getOfferModality(String offerId) {
		logger.info("Consultando modalidad(Renovable o no renovable) de la oferta  " + offerId);
		OfferModalityResponse offerModalityResponse = null;
		try {
			String url = apiUrl + getOfferModalityWs + offerId;
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			HttpEntity<String> request = new HttpEntity<>( headers);
			RestTemplate restTemplate = this.getRestTemplateJsonGET();
			ResponseEntity<OfferModalityResponse> response = restTemplate.exchange(url, HttpMethod.GET, request,
					OfferModalityResponse.class);
			int status = response.getStatusCodeValue();
			if (status == 200) {
				offerModalityResponse = response.getBody();
			}  else {
				logger.error("Error " + status);
			}
		} catch(Exception e) {
			logger.error("Error al consultar la modalidad de la oferta " + offerId );
			logger.error(e.getMessage());
		}
		return offerModalityResponse;
	}
	
	public SaldoResponse getBalanceAndExpireDate(String cellphoneNumber ) {
		logger.info("Consultando fecha de expiracion y saldo del número celular " + cellphoneNumber);
		SaldoResponse saldoResponse = null;
		try {
			String url = apiUrl + getBalanceAndExpireDateWs + cellphoneNumber;
			RestTemplate restTemplate = this.getRestTemplate();
			ResponseEntity<SaldoResponse> response = restTemplate.exchange(url, HttpMethod.GET, null,
					SaldoResponse.class);
			int status = response.getStatusCodeValue();
			if (status == 200) {
				saldoResponse = response.getBody();
			}  else {
				logger.error("Error " + status);
			}
		} catch(Exception e) {
			logger.error("Error al consultar fecha de expiracion y saldo del número celular " + cellphoneNumber);
			logger.error(e.getMessage());
		}
		
		return saldoResponse;
	}
	
}
