package com.topostechnology.sheduler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.topostechnology.service.ConektaPaymentService;
import com.topostechnology.service.ConektaSubscriptionService;

@Component
public class InvoiceExcelGeneratorTask {
	
	@Autowired
	private ConektaSubscriptionService conektaSubscriptionService;
	
	@Autowired
	private ConektaPaymentService conektaPaymentService;
	
	private static final Logger logger = LoggerFactory.getLogger(ConektaSubscriptionService.class);
	
	 @Scheduled(cron = "${subscription.invoicing.cron.expression}")
	    public void excecute(){
		 logger.info("Ejecutando tarea programada para crear excel de compras desde zonaturbo.com " + new Date());
	        conektaSubscriptionService.getSubscriptionPaidEvents(new Date());
	        conektaPaymentService.getOrderPaidEvents(new Date());
	    }

}


