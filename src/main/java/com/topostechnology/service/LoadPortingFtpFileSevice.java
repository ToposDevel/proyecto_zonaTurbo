package com.topostechnology.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.topostechnology.model.PortingModel;
import com.topostechnology.utils.StringUtils;

@Service
public class LoadPortingFtpFileSevice {
	
	@Autowired
	private FtpsService ftpsService;
	
	@Autowired
	private PortingService portingService;
	

	@Value("${local.porting.folder}")
	private String localPortingFolder;
	
	private static final Logger logger = LoggerFactory.getLogger(LoadPortingFtpFileSevice.class);
	public static final String SEPARADOR = "\\,";
	private static final String PORT_IN = "PORT-IN";
	public  final static String PORTED_STATUS  = "PORTED";
	public  final static String PORTING_OK  = "OK";
	public  final static String PORTING_DONE  = "DONE";
	
	public void processPorting() {
		try {
			List<String> newPortingFileList = ftpsService.getNewPortingFiles();
			for (String fileName : newPortingFileList) {
				String filePath = localPortingFolder + "/" + fileName;
				List<PortingModel> portingList = this.getPortings(filePath);
				for (int i = 1; i < portingList.size(); i++) { // el primer registro es encabezado
					PortingModel portingModel = portingList.get(i);
					logger.info("Portabildiad tipo " + portingModel.getPortingType());
					if (portingModel.getPortingType().equals(PORT_IN)) {
						logger.info("Portabildiad status " + portingModel.getStatus());
						if(portingModel.getStatus().equals(PORTING_OK) || portingModel.getStatus().equals(PORTING_DONE)) {
							portingModel.setStatus(PORTED_STATUS);
							portingModel.setMessage("Portabilidad completada");
							portingService.updatePortingStatus(portingModel);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Se ha generado error al descargar archivo de portabildiad SFTP");
		}
	}
	
	public List<PortingModel> getPortings(String filePath) throws ParseException {
		logger.info("Extrayendo portabilidades del archivo " + filePath);
		List<PortingModel> portings = new ArrayList<PortingModel>();
		if (Files.exists(Paths.get(filePath))) {
			File file = new File(filePath);
			if (!file.isDirectory()) {
				logger.info("Es archivo" + filePath);
				portings = getDataFromFile(filePath);
			} else {
				logger.info(file.getName() + "Es carpeta");
			}
		} else {
			logger.info(filePath + " No existe");
		}
		return portings;
	}
	
	public static List<PortingModel> getDataFromFile(String filePath)
			throws ParseException {
		List<PortingModel> portingList = new ArrayList<PortingModel>();
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
				PortingModel porting  = extractMovementInfoFromFile(data);
//				porting.setCounter(count);
				if (porting != null) {
					portingList.add(porting);
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
		return portingList;
	}
	
	private static PortingModel extractMovementInfoFromFile(String[] data)
			throws ParseException {
		PortingModel porting = null;
		if (data != null) {
			porting = new PortingModel();
			String portingType = data[1];
			String portingTypeUpper = StringUtils.isNotBlank(portingType) ? portingType.toUpperCase() : "";
			String msisdnPorted = data[2];
			String portingDate = data[5];
			String status = data[10];
			porting.setMsisdnPorted(msisdnPorted);
			porting.setPortingType(portingTypeUpper);
			porting.setExecutionDate(portingDate);
			porting.setStatus(status);
		}
		return porting;
	}
	

}
