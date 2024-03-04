package com.topostechnology;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TurboConsumoWebApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		
		 new SpringApplicationBuilder(TurboConsumoWebApplication.class)
	        .properties("spring.config.name: application")
	        .build()
	        .run(args);
			 
	}
	
}
