package com.topostechnology.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.topostechnology.rest.client.TurpoEmailManagerApiClient;
import com.topostechnology.rest.client.request.AttachFile;
import com.topostechnology.rest.client.request.SendEmailRequest;

@Service
public class EmailService {
	
	@Value("${spring.mail.username}")
	private String from;
    
	@Autowired
	private TurpoEmailManagerApiClient turpoEmailManagerApiClient;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
    public void sendSimpleAutamaticFormatNotification(String userName, String cellphoneNumber, String toEmail, String subject, String message,
			String param ) throws Exception {
    	logger.info("Enviando email a " + toEmail);
    	SendEmailRequest sendEmailRequest = this.createSendEmailRequest(userName, cellphoneNumber, toEmail, subject, message, param, null);
    	turpoEmailManagerApiClient.sendAutomaticFormatNotificationWs(sendEmailRequest);
    }
    
    public void sendSimpleAutamaticFormatNotification(String userName, String cellphoneNumber, String toEmail, String subject, String message,
			String param, List<String> files) throws Exception {
    	logger.info("Enviando email a " + toEmail);
    	SendEmailRequest sendEmailRequest = this.createSendEmailRequest(userName, cellphoneNumber, toEmail, subject, message, param, files);
    	turpoEmailManagerApiClient.sendAutomaticFormatNotificationWs(sendEmailRequest);
    }
    
    public void sendEmailWithTemplateWs(String toEmail, String subject, String message) throws Exception {
    	logger.info("Enviando email a " + toEmail);
    	String[] toList = {toEmail};
    	SendEmailRequest sendEmailRequest  = new SendEmailRequest();
    	sendEmailRequest.setFrom(from);
    	sendEmailRequest.setMessage(message);
    	sendEmailRequest.setSubject(subject);
    	sendEmailRequest.setTo(toList);
    	turpoEmailManagerApiClient.sendEmailWithTemplateWs(sendEmailRequest);
    }
    
    private SendEmailRequest createSendEmailRequest(String userName, String cellphoneNumber, String toEmail, String subject, String message,
			String param, List<String> files) throws Exception {
    	SendEmailRequest sendEmailRequest  = new SendEmailRequest();
    	String[] toList = {toEmail};
    	sendEmailRequest.setUserName(userName);
    	sendEmailRequest.setCellphoneNumber(cellphoneNumber);
    	sendEmailRequest.setFrom(from);
    	sendEmailRequest.setMessage(message);
    	sendEmailRequest.setSubject(subject);
    	sendEmailRequest.setTo(toList);
    	
    	if(files != null) {
    		List<AttachFile> attachFiles = new ArrayList<AttachFile>();
    		for(String filePath: files) {
    			logger.info("Adjuntando " + filePath);
    			byte[] imageBytes = Files.readAllBytes(Paths.get(filePath));
    			AttachFile attachFile = new AttachFile();
    			File file = new File(filePath);
    			attachFile.setFileName(file.getName());
    			attachFile.setFilePath(filePath);
    			attachFile.setFile(imageBytes);
    			attachFiles.add(attachFile);
        	}
    		sendEmailRequest.setAttachFiles(attachFiles);
    	}

    	return sendEmailRequest;
    }
	
}
