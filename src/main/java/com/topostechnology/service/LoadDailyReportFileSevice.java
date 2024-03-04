package com.topostechnology.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
public class LoadDailyReportFileSevice {

	private static final Logger logger = LoggerFactory.getLogger(LoadDailyReportFileSevice.class);

	public void process() {
		try {
			List<DailyReportModel> records = getRecords();
		} catch (Exception e) {
			logger.error("Se ha generado error al cargar archivo de reporte diario");
		}
	}
	
	@SuppressWarnings("resource")
	private List<DailyReportModel> getRecords() throws IOException {
			List<DailyReportModel> recordList = new ArrayList<DailyReportModel>();
			File file = new File("C:\\Apps\\report\\Export_BE_152_20122021.xlsx");
			InputStream inputStream = new FileInputStream(file);
			XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
			XSSFSheet worksheet = workbook.getSheetAt(0);
			for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
				if (index > 0) {
					DailyReportModel record = new DailyReportModel();
					XSSFRow row = worksheet.getRow(index);
					record.setStatus(row.getCell(2).getStringCellValue());
					record.setAssociatedNumber(row.getCell(3).getStringCellValue());
					record.setImsi(row.getCell(4).getStringCellValue());
					record.setOfferIdd(row.getCell(5).getStringCellValue());
					record.setOfferName(row.getCell(6).getStringCellValue());
					record.setEffDate(row.getCell(7).getStringCellValue());
					recordList.add(record);
				}
			}
			return recordList;
	}

		

}
