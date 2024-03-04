package com.topostechnology.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.topostechnology.constant.CdrConstants;
import com.topostechnology.domain.Offer;
import com.topostechnology.domain.User;
import com.topostechnology.mapper.BalanceMapper;
import com.topostechnology.model.BalanceModel;
import com.topostechnology.model.FUConsumptionDetailModel;
import com.topostechnology.model.MonthModel;
import com.topostechnology.repository.CdrRepository;
import com.topostechnology.repository.OfferRepository;
import com.topostechnology.rest.client.GategayApiClient;
import com.topostechnology.rest.client.response.SelfConsumption;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

@Service
public class FUConsumptionService {
	
	private static final Logger logger = LoggerFactory.getLogger(FUConsumptionService.class);

	@Value("${turbored.logo.path}")
	private String turboLogoPath;

	@Value("${turbored.logo.path}")
	private String turboredLogoPath;
	
	@Value("${table.cdr.name.cbs}")
	private String tableNameCbs;

	@Value("${table.cdr.name.sms}")
	private String tableNameSms;

	@Value("${table.cdr.name.voice}")
	private String tableNameVoice;
	
	@Value("${mx.country.code}")
	private String mxCountryCode;
	
	@Autowired
	private CdrRepository cdrRepository;
	
	@Autowired
	private GategayApiClient gategayApiClient;
	
	@Autowired
	private OfferRepository offerRepository;
	
	public List<MonthModel> getLastMonths(int numberLastMonths) {
		return GeneralCdrService.getLastMonths(numberLastMonths);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional
	public byte[] generatePDF(User user, String monthYear) throws IOException, JRException {
		String  cellphoneNumber = user.getPhones().get(0).getCellphoneNumber();
		Map<String, Object> params = new HashMap<>();
		params.put("LOGO", getLogoAsInputSream("images/turbored_logo.png"));
		params.put("MSISDN", cellphoneNumber);
		LocalDateTime initDate = GeneralCdrService.getInitMonthDateTime(monthYear);
		Instant instantInitDate = initDate.atZone(ZoneId.systemDefault()).toInstant();
		params.put("FROM_DATE", Date.from(instantInitDate));
		LocalDateTime endDate = GeneralCdrService.getEndMonthDateTime(initDate.toLocalDate());
		Instant instanteEndDate = endDate.atZone(ZoneId.systemDefault()).toInstant();
		params.put("TO_DATE", Date.from(instanteEndDate));
		params.put("USER_NAME", user.getName() + " " +user.getLastName());
		
		Offer offer = this.getOfferByCellPhone(cellphoneNumber);
		params.put("OFFER_NAME", offer != null ? offer.getCommercialName() : "");

		long startTime = System.currentTimeMillis();
		logger.info("EMPIEZA: " + new Date());

		List<FUConsumptionDetailModel> itemsDetail = this.getConsumptionDetail(monthYear, cellphoneNumber); 
		
		long endTime = System.currentTimeMillis();
		
		JRBeanCollectionDataSource itemsRrBean = new JRBeanCollectionDataSource(itemsDetail);
		
		long duration = (endTime - startTime);
		
		logger.info("TERMINA: " + new Date());
		logger.info("Tiempo para extraer registros" + duration);
		
		params.put("ConsumptionDetail", itemsRrBean);
		
		// IDIOMA DEL REPORTE
		Locale locale = new Locale("es", "ES");
		params.put(JRParameter.REPORT_LOCALE, locale);

		JasperPrint jasperPrint = JasperFillManager.fillReport(
				getClass().getClassLoader().getResourceAsStream("reports/detalle_consumo.jasper"), params,
				new JREmptyDataSource());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		JRAbstractExporter exporter = new JRPdfExporter();
		SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
		configuration.setCompressed(true);
		exporter.setConfiguration(configuration);
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
		exporter.exportReport();
		return outputStream.toByteArray();
	}
	
	public List<FUConsumptionDetailModel> getConsumptionDetail(String monthYear, String cellphoneNumber) {
		String msisdn = mxCountryCode + cellphoneNumber;
		List<FUConsumptionDetailModel> consumptionDetailList = getCdrByMsisdnInfo(monthYear, msisdn);
		List<FUConsumptionDetailModel> consumptionDetailSortedList = consumptionDetailList.stream().sorted(Comparator.comparing(FUConsumptionDetailModel :: getDate))
                .collect(Collectors.toList());
		return consumptionDetailSortedList;
	}
	
	private List<FUConsumptionDetailModel> getCdrByMsisdnInfo(String monthYear, String msisdn){
		LocalDateTime initDateTime = GeneralCdrService.getInitMonthDateTime(monthYear);
		LocalDateTime endDateTime = GeneralCdrService.getEndMonthDateTime(initDateTime.toLocalDate()); 
		List<Object[]> rows = cdrRepository.getCdrInfoByMsisdnByDate(initDateTime, endDateTime, msisdn); 
		return convertRowsToConsumptionDetail(rows);
	}
	
	private List<FUConsumptionDetailModel> getCdrByMsisdnInfo0(String monthYear, String msisdn, String cdrTableName, String consumptionType){ // TODO
		LocalDateTime initDateTime = GeneralCdrService.getInitMonthDateTime(monthYear);
		LocalDateTime endDateTime = GeneralCdrService.getEndMonthDateTime(initDateTime.toLocalDate()); 
		List<Object[]> rows = cdrRepository.getCdrInfoByMsisdnByDate0(initDateTime, endDateTime, msisdn, cdrTableName); 
		return convertRowsToConsumptionDetail(rows);
	}
	
	public List<FUConsumptionDetailModel> getConsumptionDetail0(String monthYear, String cellphoneNumber) { // TODO
		String msisdn = mxCountryCode + cellphoneNumber;
		List<FUConsumptionDetailModel> consumptionDetailList = new ArrayList<FUConsumptionDetailModel>();
		consumptionDetailList.addAll(getCdrByMsisdnInfo0(monthYear, msisdn, tableNameCbs, CdrConstants.CDR_DATA_TYPE));
		consumptionDetailList.addAll(getCdrByMsisdnInfo0(monthYear,  msisdn, tableNameSms, CdrConstants.CDR_SMS_TYPE));
		consumptionDetailList.addAll(getCdrByMsisdnInfo0(monthYear, msisdn, tableNameVoice, CdrConstants.CDR_VOICE_TYPE));
		List<FUConsumptionDetailModel> consumptionDetailSortedList = consumptionDetailList.stream().sorted(Comparator.comparing(FUConsumptionDetailModel :: getDate))
                .collect(Collectors.toList());
		return consumptionDetailSortedList;
	}
	
	
	private List<FUConsumptionDetailModel> convertRowsToConsumptionDetail(List<Object[]> rows){
		List<FUConsumptionDetailModel> consumptionDetailList = new ArrayList<>(rows.size());
		for (Object[] row : rows) {
			FUConsumptionDetailModel consumptionDetail = new FUConsumptionDetailModel();
			BigInteger cdrId = (BigInteger) row[0];
			consumptionDetail.setCdrId(cdrId.longValue());
			consumptionDetail.setDate((Date) row[1]);
			BigInteger quantity = (BigInteger) row[2];
			consumptionDetail.setQuantity(quantity.longValue());
			int measurementUnitId = (Integer) row[3];
			consumptionDetail.setMeasurementUnit(this.getMeasurementUnitName(measurementUnitId));
			consumptionDetail.setType(this.getConsumptionType(measurementUnitId));
			consumptionDetailList.add(consumptionDetail);
		}
		return consumptionDetailList;
	}
	
	private String getMeasurementUnitName(int measurementUnitId) {
		String measurementUnit = null;
		if (measurementUnitId == CdrConstants.CDR_MEASUREMENT_UNIT_1003_ID) {
			measurementUnit = CdrConstants.CDR_MEASUREMENT_UNIT_1003_NAME;
		} else if (measurementUnitId == CdrConstants.CDR_MEASUREMENT_UNIT_1101_ID) {
			measurementUnit = CdrConstants.CDR_MEASUREMENT_UNIT_1101_NAME;
		} else if (measurementUnitId == 1106) {
			measurementUnit = CdrConstants.CDR_MEASUREMENT_UNIT_1106_NAME;
		}
		return measurementUnit;
	}

	private String getConsumptionType(int measurementUnitId) {
		String consumptioType=null;
		if(measurementUnitId == CdrConstants.CDR_MEASUREMENT_UNIT_1003_ID) {
			consumptioType = CdrConstants.CDR_SMS_TYPE;
		} else if(measurementUnitId == CdrConstants.CDR_MEASUREMENT_UNIT_1101_ID) {
			consumptioType = CdrConstants.CDR_VOICE_TYPE;
		} else if(measurementUnitId == CdrConstants.CDR_MEASUREMENT_UNIT_1106_ID) {
			consumptioType = CdrConstants.CDR_DATA_TYPE;
		}
		return consumptioType;
	}


	private InputStream getLogoAsInputSream(String path) throws FileNotFoundException {
		return new FileInputStream(new File(turboLogoPath));
	}
	
	@Transactional
	public boolean validateMsisdn(String cellphoneNumber) {
		boolean valid = false;
		String msisdn = mxCountryCode + cellphoneNumber;
		BigInteger resultInteger;
		Long count;
		Object result;
		result = cdrRepository.countMsisd(msisdn, tableNameCbs);
		resultInteger = (BigInteger) result;
		count = resultInteger.longValue();
		if (count == 0) {
			result = cdrRepository.countMsisd(msisdn, tableNameSms);
			resultInteger = (BigInteger) result;
			count = resultInteger.longValue();
			if (count == 0) {
				result = cdrRepository.countMsisd(msisdn, tableNameVoice);
				resultInteger = (BigInteger) result;
				count = resultInteger.longValue();
				if (count == 0) {
					valid = false;
				} else {
					valid = true;
				}
			} else {
				valid = true;
			}
		} else {
			valid = true;
		}
		return valid;
	}
	
	@Transactional
	public BalanceModel getBalance(String cellphone) {
		logger.info("getBalance() " + cellphone );
		SelfConsumption selfConsumption = gategayApiClient.getConsumption(cellphone);
		Offer offer = this.getOfferByCellPhone(cellphone);
		BalanceModel balanceModel = null;
		if(selfConsumption != null ) {
			balanceModel = BalanceMapper.convertSelfConsumptionToBalanceModel(selfConsumption);
			if( offer != null) {
				balanceModel.setOfferId(offer.getOfferId());
				balanceModel.setOfferName(offer.getName());
				balanceModel.setCommercialName(offer.getCommercialName());
			} else {
				balanceModel.setOfferName("");
				balanceModel.setCommercialName("");
			}
		}
		return balanceModel;
	}
	
	public Offer getOfferByCellPhone(String cellphone) {
		Offer offer = null;
		String offerId = gategayApiClient.getOfferId(cellphone);
		logger.info("Offer id: " + offerId);
		if(offerId != null) {
			offer = offerRepository.findByOfferId(Long.valueOf(offerId));
			if(offer != null) {
				logger.info("Offer name: " + offer.getCommercialName());
			} else {
				logger.info("No se encontro oferta registrada en bd para " + offerId);
			}
		}
		return offer;
	}

}
