package com.topostechnology.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.topostechnology.model.OdooInvoicing;
import com.topostechnology.utils.DateUtils;

@Service
public class InvoicingService {

	private static final Logger logger = LoggerFactory.getLogger(ConektaSubscriptionService.class);
	private static final String XLS_EXTENSION = ".xls";
	private static final String CSV_EXTENSION = ".csv";
	
	@Value("${oddo.invoice.path}")
	private String odooInvoicingPath;
	
	@Value("${oddo.invoice.email}")
	private String oddoInvoiceEmail;
	
	@Autowired
	private EmailService emailService;
	private Workbook workbook;
	
	public void createInvoiceExcel(List<OdooInvoicing> odooInvoicingList, String invoicingType) throws FileNotFoundException, IOException {
		logger.info("Generando el excel...");
		String [] headers = new String[]{ "Número",	"Icono Factura Proveedor",	"Nombre del socio a mostrar en la factura",	"Fecha de Factura/Recibo",
				"Origen", "	Comercial", "Fecha vencimiento", "Importe sin impuestos con signo",	"Total con signo",
				"Importe adeudado con signo", "Estado de factura", 	"Estado", "Pagos", "SIM CARDS", "SIM CARDS/Producto"};
		int colNum = 0;
		int rowCount = 0;
		workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet("Sheet1");
		Row rowHeader = sheet.createRow(0);
		CellStyle headerStyle = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short)12);
		font.setFontName("Arial");
		font.setBold(true);
		headerStyle.setFont(font);
		
        for (String headerName : headers) {
        	Cell headerCell = rowHeader.createCell(colNum);
    		headerCell.setCellValue(headerName);
    		headerCell.setCellStyle(headerStyle);
            ++colNum;
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
		for (OdooInvoicing item : odooInvoicingList) {
			Row row = sheet.createRow(++rowCount);
			int columnCount = 0;
			
			Cell cell = row.createCell(columnCount);
			cell.setCellValue((String) item.getNumber());
			cell = row.createCell(++columnCount);
			cell.setCellValue((String) item.getInvoiceIcon());
			cell = row.createCell(++columnCount);
			cell.setCellValue((String) item.getCustomerName());
			cell = row.createCell(++columnCount);
			cell.setCellValue((String) item.getInvoiceDate());
			cell = row.createCell(++columnCount);
			cell.setCellValue((String) item.getOrigin());
			cell = row.createCell(++columnCount);
			cell.setCellValue((String) item.getCommercial());
			cell = row.createCell(++columnCount);
			cell.setCellValue((String) item.getExpirationDate());
			cell = row.createCell(++columnCount);
			cell.setCellValue((String) item.getSubtotal());
			cell = row.createCell(++columnCount);
			cell.setCellValue((String) item.getTotal());
			cell = row.createCell(++columnCount);
			cell.setCellValue((String) item.getDebt());
			cell = row.createCell(++columnCount);
			cell.setCellValue((String) item.getInvoiceStatus());
			cell = row.createCell(++columnCount);
			cell.setCellValue((String) item.getStatus());
			cell = row.createCell(++columnCount);
			cell.setCellValue((String) item.getPayments());
			cell = row.createCell(++columnCount);
			cell.setCellValue((String) item.getCellphoneNumber());
			cell = row.createCell(++columnCount);
			cell.setCellValue((String) item.getOfferId());
		}
		String yesterday = DateUtils.formatDate( DateUtils.addOrRemoveDays(new Date(), -1), DateUtils.YYYYMMDD_FORMAT);
		String fileXlsPath = odooInvoicingPath + invoicingType + yesterday+  XLS_EXTENSION;
		List<String> files = new ArrayList<String>();
		files.add(fileXlsPath);
		try (FileOutputStream outputStream = new FileOutputStream( fileXlsPath)) {
			workbook.write(outputStream);
			emailService.sendSimpleAutamaticFormatNotification(null, null, oddoInvoiceEmail, 
					invoicingType + yesterday,
					"Relacion de " + invoicingType + " pagadas el día " + yesterday + ".", 
					null, 
					files);
		} catch (Exception e) {
			logger.error("Error al crear y enviar correo con relacion de  " + invoicingType  
					+" Error msg " + e.getMessage());
		}
		logger.info("Excel de facturacion de " + invoicingType +" ha sido generado.");

	}

}
