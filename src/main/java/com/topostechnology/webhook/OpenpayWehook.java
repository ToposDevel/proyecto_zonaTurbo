package com.topostechnology.webhook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.topostechnology.service.OpenpayWehookService;
import com.topostechnology.webhook.data.OpenpayEventData;
import com.topostechnology.webhook.data.WebhookResponse;

@RestController
@RequestMapping("/webhook")
public class OpenpayWehook {

	private static final Logger logger = LoggerFactory.getLogger(OpenpayWehook.class);
	
	@Autowired
	private OpenpayWehookService openpayWehookService;

	@PostMapping(path = "/openpay-events/")
	public ResponseEntity<String> receiveEvent(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.info("Recibiendo notificación");
		String jsonResponse = IOUtils.toString(request.getReader());
		logger.info(jsonResponse);
		logger.info("Response status " + response.getStatus() );
		logger.info(jsonResponse);
		OpenpayEventData openpayEventData = new Gson().fromJson(jsonResponse, OpenpayEventData.class);
		openpayWehookService.proccessEvents(openpayEventData);
		try {
			// TODO
		} catch (Exception e) {
			logger.info("Se ha generado un error al procesar Evento de webhook." + e.getMessage());
			e.printStackTrace();
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

}
