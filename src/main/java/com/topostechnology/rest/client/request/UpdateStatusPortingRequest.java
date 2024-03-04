package com.topostechnology.rest.client.request;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter	
public class UpdateStatusPortingRequest {
	
	@NotNull
	@Digits(integer = 10, fraction = 0,  message="El número de celular deben ser 10 digitos numéricos")
	@Size(min = 10, max = 10  , message ="El número de celular debe contener 10 dígitos")
	private String cellphoneNumber;
	
	@NotNull
	@NotEmpty
	private String status;


	@NotNull
	@NotEmpty
	private String message;

	
	private String portingDate;
	
	private String  rida;
	
	private String rcr;
	
	
}

