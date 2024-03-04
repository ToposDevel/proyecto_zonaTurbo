package com.topostechnology.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.topostechnology.constant.CoverageConstants;
import com.topostechnology.constant.ResponseCodeConstants;
import com.topostechnology.dto.CoverageDto;
import com.topostechnology.exception.TrException;
import com.topostechnology.model.ValidateCoverageModel;
import com.topostechnology.rest.client.GategayApiClient;
import com.topostechnology.rest.client.TurboApiClient;
import com.topostechnology.rest.client.response.CoordinatesResponse;
import com.topostechnology.rest.client.response.CoverageResponse;

@Service
public class CoverageService {

	private static final Logger logger = LoggerFactory.getLogger(CoverageService.class);

	private static final String COMA = ",";

	@Autowired
	private GategayApiClient gategayApiClient;

	@Autowired
	private TurboApiClient turboApiClient;
	
	@Value("${home.internet.20.url}")
	private String 	homeInternet20Url;
	
	@Value("${home.internet.10.url}")
	private String 	homeInternet10Url;
	
	@Value("${home.internet.5.url}")
	private String 	homeInternet5Url;
	
	@Value("${coppel.home.internet.20.url}")
	private String 	coppelHomeInternet20Url;
	
	@Value("${coppel.home.internet.10.url}")
	private String 	coppelHomeInternet10Url;
	
	@Value("${coppel.home.internet.5.url}")
	private String 	coppelHomeInternet5Url;
	
	@Value("${contact.us.url}")
	private String contactUsUrl;
	
	@Value("${mifi.plans.url}")
	private String mifiPlansUrl;
	
	@Transactional
	public void validateCoverage(ValidateCoverageModel validateCoverageModel) throws Exception {
		String coordinates = validateCoverageModel.getCoordinates();
		if (validateCoverageModel.getCoordinates() == null || validateCoverageModel.getCoordinates().isEmpty()) { // SI  NO  VIENEN COORDENADAS HAY QUE  CONSULTARLAS
			CoordinatesResponse coordinatesResponse = turboApiClient.getCoordinates(validateCoverageModel.getAddress(), validateCoverageModel.getZipcode());
			if (coordinatesResponse.getCode() == ResponseCodeConstants.ERROR_CODE) {
				throw new TrException(coordinatesResponse.getMessage());
			} else {
				coordinates = formatCoordinates(coordinatesResponse.getLat(), coordinatesResponse.getLng());
				validateCoverageModel.setFoundAddress(coordinatesResponse.getFormattedAddress());
			}
		}
		List<CoverageDto> coverageList = validateCoordinates(coordinates, validateCoverageModel.getOrigin());
		validateCoverageModel.setCoverageList(coverageList);
	}
	
	public List<CoverageDto> validateCoordinates(String coordinates,  String origin) throws Exception {
		logger.info("Validando serviciabilidad");
		List<CoverageDto> coverageList = new ArrayList<CoverageDto>();
		if (coordinates != null) {
			CoverageResponse coverageResponse =  gategayApiClient.validateServiceability(coordinates);
			String result = coverageResponse.getResult();
			String description = coverageResponse.getDescription() != null ? coverageResponse.getDescription(): "";
			if (result != null) {

				// Se obtiene url para origen
				String internet20Url = "";
				String internet10Url = "";
				String internet5Url = "";
				if(origin.equals(CoverageConstants.COVERAGE_ORIGIN)) {
					internet20Url = homeInternet20Url;
					internet10Url = homeInternet10Url;
					internet5Url = homeInternet5Url;
				} else if(origin.equals(CoverageConstants.COVERAGE_COPPEL_ORIGIN)) {
					internet20Url = coppelHomeInternet20Url;
					internet10Url = coppelHomeInternet10Url;
					internet5Url = coppelHomeInternet5Url;
				}

				CoverageDto coverageDto20 = new CoverageDto();
				coverageDto20.setSpeed(CoverageConstants.TWENTY_MPBPS);
				coverageDto20.setPlanUrl(internet20Url);
				coverageDto20.setPlanName(CoverageConstants.HOME_INTERNET_20);
				CoverageDto coverageDto10 = new CoverageDto();
				coverageDto10.setSpeed(CoverageConstants.TEN_MPBPS);
				coverageDto10.setPlanUrl(internet10Url);
				coverageDto10.setPlanName(CoverageConstants.HOME_INTERNET_10);
				CoverageDto coverageDto5 = new CoverageDto();
				coverageDto5.setSpeed(CoverageConstants.FIVE_MPBPS);
				coverageDto5.setPlanUrl(internet5Url);
				coverageDto5.setPlanName(CoverageConstants.HOME_INTERNET_5);
				String resultFormated = result.trim().toUpperCase();
				if (resultFormated.equals(CoverageConstants.E_BLK) 
						|| resultFormated.equals(CoverageConstants.E_RES) 
						|| resultFormated.equals(CoverageConstants.E_WOC)
						|| resultFormated.equals(CoverageConstants.E_NSL)
						|| resultFormated.equals(CoverageConstants.WITHOUT_SERVICEABILITY)) {
					CoverageDto coverageWithoutServiceDto = new CoverageDto();
					coverageWithoutServiceDto.setSpeed(CoverageConstants.WITHOUT_SERVICEABILITY_SPANISH);
					coverageWithoutServiceDto.setPlanName(CoverageConstants.SHOW_MIFI_PLANS);
					coverageWithoutServiceDto.setPlanUrl(mifiPlansUrl);
					coverageList.add(coverageWithoutServiceDto);
				} else {
					switch (resultFormated) {
					case CoverageConstants.BROADBAND_TWENTY:
						coverageList.add(coverageDto5);
						if(!description.equals(CoverageConstants.HIGH_OCCUPANCY)) {
							coverageList.add(coverageDto20);
							coverageList.add(coverageDto10);
						}
						break;
					case CoverageConstants.BROADBAND_TEN:
						coverageList.add(coverageDto5);
						if(!description.equals(CoverageConstants.HIGH_OCCUPANCY)) {
							coverageList.add(coverageDto10);
						}
						break;
					case CoverageConstants.BROADBAND_FIVE:
						coverageList.add(coverageDto5);
						break;
					default:
						CoverageDto coverageDto = new CoverageDto();
						coverageDto.setSpeed(resultFormated);
						coverageList.add(coverageDto);
					}
				}
			}
		}
		return coverageList;
	}
	
	public boolean hasCoverage(String coordinates) throws Exception {
		logger.info("Validando si hay servicio de altan");
		boolean hasCoverage = false;
		if (coordinates != null) {
			CoverageResponse coverageResponse = gategayApiClient.validateServiceability(coordinates);
			String result = coverageResponse.getResult();
			if (result != null) {
				String resultFormated = result.trim().toLowerCase();
				if (!resultFormated.equals(CoverageConstants.E_BLK)) {
					hasCoverage = true;
				}
			}
		} else {
			throw new TrException("Las coordenadas son requeridas.");
		}
		return hasCoverage;
	}

	public String validateFields(ValidateCoverageModel validateCoverageModel) {
		String validationMessage = null;
		String addres = validateCoverageModel.getAddress() != null ? validateCoverageModel.getAddress().trim() : "";
		String zipcode = validateCoverageModel.getZipcode() != null ? validateCoverageModel.getZipcode().trim() : "";
		String coordinates = validateCoverageModel.getCoordinates() != null
				? validateCoverageModel.getCoordinates().trim()
				: "";
		if (addres.isEmpty() && coordinates.isEmpty() && zipcode.isEmpty()) {
			validationMessage = "Es necesario ingresar alguno de los campos de búsqueda.";
		}
		return validationMessage;
	}
	
	private String formatCoordinates(double lat, double lng) {
		String coordinates = String.valueOf(lat) + COMA + String.valueOf(lng); 
		return coordinates;
	}

}

