package com.topostechnology.sheduler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.topostechnology.service.ConektaSubscriptionService;
import com.topostechnology.service.LoadPortingFtpFileSevice;

@Component
public class LoadPortingSftpTask {
	
	private static final Logger logger = LoggerFactory.getLogger(ConektaSubscriptionService.class);

	@Autowired
	private LoadPortingFtpFileSevice loadPortingFtpFileSevice;
	
	
	 @Scheduled(cron = "${load.porting.sftp.cron.expression}")
	    public void excecute(){
		 logger.info("Ejecutando tarea programada para hacer suscripciones programadas"  + new Date());
		 loadPortingFtpFileSevice.processPorting();
	    }

}