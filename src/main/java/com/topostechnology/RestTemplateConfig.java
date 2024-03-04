package com.topostechnology;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
public class RestTemplateConfig{
	
	static final int TIMEOUT = 60000;

	@Bean
	RestTemplate restTemplateWithConnectReadTimeout() {  
	    return new RestTemplateBuilder()
	        .setConnectTimeout(Duration.ofMillis(TIMEOUT))
	        .setReadTimeout(Duration.ofMillis(TIMEOUT))
	        .build();
	}
	
	
}
	
