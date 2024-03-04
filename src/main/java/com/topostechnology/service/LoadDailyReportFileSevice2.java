package com.topostechnology.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.topostechnology.model.DailyReportModel;

@Service
public class LoadDailyReportFileSevice2 {
	
	public static final String SEPARADOR = "\\,";

	private static String localFolder = "C:/Apps/report";
	private static String localDailyFile = "Export_BE_152_20122021.xlsx";
	
	
	private static final Logger logger = LoggerFactory.getLogger(LoadDailyReportFileSevice2.class);
	
	public static void process() {
		try {
				String filePath = localFolder + "/" + localDailyFile;
				List<DailyReportModel> dailyList = getNumbers(filePath);
				for (int i = 1; i < dailyList.size(); i++) { // el primer registro es encabezado
					DailyReportModel record = dailyList.get(i);
					System.out.println(record.getAssociatedNumber());
					
					// TODO save in DB
				}
		} catch (Exception e) {
			logger.error("Se ha generado error al descargar archivo de reporte diarios");
		}
	}
	
	public static List<DailyReportModel> getNumbers(String filePath) throws ParseException {
		logger.info("Extrayendo registros del archivo " + filePath);
		List<DailyReportModel> dailyList = new ArrayList<DailyReportModel>();
		if (Files.exists(Paths.get(filePath))) {
			File file = new File(filePath);
			if (!file.isDirectory()) {
				logger.info("Es archivo" + filePath);
				dailyList = getDataFromFile(filePath);
			} else {
				logger.info(file.getName() + "Es carpeta");
			}
		} else {
			logger.info(filePath + " No existe");
		}
		return dailyList;
	}
	
	public static List<DailyReportModel> getDataFromFile(String filePath)
			throws ParseException {
		List<DailyReportModel> dailyList = new ArrayList<DailyReportModel>();
		BufferedReader br = null;
		String[] data = null;
		String line = "";
		try {
			br = new BufferedReader(new FileReader(filePath));
			int count = 0;
			while ((line = br.readLine()) != null) {
				count ++;
				data = line.split(SEPARADOR);
				logger.info("line " + count + "==>" + line); // TODO QUITAR
				DailyReportModel daylyModel  = extractMovementInfoFromFile(data);
				if (daylyModel != null) {
					dailyList.add(daylyModel);
				}
			}
			logger.info("Numero de registros " + count);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return dailyList;
	}
	
	private static DailyReportModel extractMovementInfoFromFile(String[] data)
			throws ParseException {
		DailyReportModel daylyModel = null;
		if (data != null) {
			daylyModel = new DailyReportModel();
			daylyModel.setAssociatedNumber( data[3]);
		}
		return daylyModel;
	}
	
	
//	public static void main(String args[]) {
//		
//		 File f = new File("C:\\Apps\\report\\Export_BE_152_20122021.xlsx");
//	       InputStream inp = new FileInputStream(f);
//	       Workbook wb = WorkbookFactory.create(inp);
//	       sheet = wb.getSheetAt(0);
//	       Row row = sh.getRow(iRow); //En qué fila empezar ya dependerá también de si tenemos, por ejemplo, el título de cada columna en la primera fila
//	       while(row!=null) 
//	       {
//	           Cell cell = row.getCell(1);  
//	           String value = cell.getStringCellValue();
//	           System.out.println("Valor de la celda es " + value);
//	           iRow++;  
//	           row = sh.getRow(iRow);
//	       }
//	}
	
	public static void main(String []args) throws IOException {
		 List<DailyReportModel> productList = new ArrayList<>();
		 File file = new File("C:\\Apps\\report\\Export_BE_152_20122021.xlsx");
		 InputStream inputStream = new FileInputStream(file);
	        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
	        XSSFSheet worksheet = workbook.getSheetAt(0);
	        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
	            if (index > 0) {
	            	DailyReportModel record = new DailyReportModel();

	                XSSFRow row = worksheet.getRow(index);
	                String status = row.getCell(2).getStringCellValue();
	                String associatedNumber = row.getCell(3).getStringCellValue();
	                String imsi = row.getCell(4).getStringCellValue();
	                String offerId = row.getCell(5).getStringCellValue();
	                String offerName = row.getCell(6).getStringCellValue();
	                String effDate = row.getCell(7).getStringCellValue();
	                System.out.println(status);
	                System.out.println(associatedNumber);
	                System.out.println(imsi);
	                System.out.println(offerId);
	                System.out.println(offerName);
	                System.out.println(effDate);
	                
	                
	                record.setStatus(row.getCell(2).getStringCellValue());
	                record.setAssociatedNumber(row.getCell(3).getStringCellValue());
	                record.setImsi(row.getCell(4).getStringCellValue());
	                record.setOfferIdd(row.getCell(5).getStringCellValue());
	                record.setOfferName(row.getCell(6).getStringCellValue());
	                record.setEffDate(row.getCell(7).getStringCellValue());
	                productList.add(record);
	            }
	        }
	        
	        System.out.println(productList.size());
	}

}
