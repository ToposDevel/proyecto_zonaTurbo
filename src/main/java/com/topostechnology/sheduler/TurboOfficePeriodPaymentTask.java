package com.topostechnology.sheduler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.topostechnology.service.ConektaSubscriptionService;
import com.topostechnology.service.TurboOfficeService;

@Component
public class TurboOfficePeriodPaymentTask {
	
	private static final Logger logger = LoggerFactory.getLogger(ConektaSubscriptionService.class);

	@Autowired
	private TurboOfficeService turboOfficeService;
	
	
	 @Scheduled(cron = "${turbo.office.periodic.payment.cron.expression}")
	    public void excecute(){
		 logger.info("Ejecutando tarea programada para PAGO PERDIODICO de turbo TURBO OFFICE"  + new Date());
		 try {
			turboOfficeService.processPeriodPayments();
		} catch (Exception e) {
			logger.error("El proceso de PAGO PERDIODICO de turbo TURBO OFFICE no pudo ser completado " + e.getMessage());
		}
	 }

}