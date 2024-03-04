package com.topostechnology.sheduler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.topostechnology.rest.client.TurboApiClient;


@Component
public class VirtualNumberLiberatorTask {
	
	private static final Logger logger = LoggerFactory.getLogger(VirtualNumberLiberatorTask.class);
	
	@Autowired
	private TurboApiClient turboApiClient;
	
	@Scheduled(cron = "${virtual.number.liberator.cron.expression}")
    public void excecute(){
	Date nowDate = new Date();
	 logger.info("Ejecutando tarea programada para liberar numeros virtuales"  + nowDate);
	 try {
		turboApiClient.unfreezeNumbers(nowDate);
	} catch (Exception e) {
		logger.error("Los numeros no pudieron ser descongelados " + e.getMessage());
	}
    }

}
