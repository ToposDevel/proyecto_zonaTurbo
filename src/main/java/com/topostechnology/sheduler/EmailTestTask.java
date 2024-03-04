package com.topostechnology.sheduler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.topostechnology.service.EmailService;


@Component
public class EmailTestTask {

	private static final Logger logger = LoggerFactory.getLogger(EmailTestTask.class);

	@Autowired
	private EmailService emailService;

	@Value("${turbored.operation.email}")
	private String operationEmail;

	@Scheduled(cron = "${send.email.test.cron.expression}")
	public void excecute() {
		logger.info("Probando el servicio de correo  " + new Date());
		try {
			emailService.sendSimpleAutamaticFormatNotification(null, null, operationEmail, "Testing email",
					"Testing email " + new Date(), null);
		} catch (Exception e) {
			logger.error("No pudo ser enviado el correo a " + operationEmail + "Error " + e.getMessage());
		}
	}

}
