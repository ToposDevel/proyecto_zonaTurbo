package com.topostechnology.rest.client.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortingRequest {
	
	public PortingRequest( ) {
	}
	
	public PortingRequest(String fecha, String numero, String pin ) {
		this.fecha = fecha;
		this.numero= numero;
		this.pin = pin;
	}
	
	private String fecha;
	private String numero;
	private String pin;

}
