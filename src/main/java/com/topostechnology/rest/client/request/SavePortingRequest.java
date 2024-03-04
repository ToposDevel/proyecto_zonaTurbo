package com.topostechnology.rest.client.request;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavePortingRequest {
	
	private String fullName;
	private String email;
	
	@NotNull
	@Digits(integer = 10, fraction = 0,  message="El número de celular deben ser 10 digitos numéricos")
	@Size(min = 10, max = 10  , message ="El número de celular debe contener 10 dígitos")
	private String msisdnPorted;
	
	@NotNull
	@Digits(integer = 10, fraction = 0,  message="El número de celular deben ser 10 digitos numéricos")
	@Size(min = 10, max = 10  , message ="El número de celular debe contener 10 dígitos")
	private String msisdnTransitory;
	
	@NotNull
	private String imsi;
	
	@NotNull
	private String iccid;
	
	@NotNull
	@NotEmpty
	private String nip;
	
	@NotNull
	@NotEmpty
	private String date;

	private String status;

	@NotNull
	@NotEmpty
	private String folioId;
	
	@NotNull
	@NotEmpty
	private String portId;
	
	private String message;

	private String approvedDateABD;
	
	private String  dida;
	
	@NotNull
	@NotEmpty
	private String  rida;
	
	private String dcr;
	
	@NotNull
	@NotEmpty
	private String rcr;
	
	private String autoScriptReg;
	
	public SavePortingRequest( ) {
	}
	
}
