package com.topostechnology.rest.client.request;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserValidationCodeRequest {
	
	private String code;
	
	@NotNull(message="must not be null")
	@Digits(integer = 10, fraction = 0,  message="only numbers are accepted")
	@Size(min = 10, max = 10  , message =":the minimum length is 10 characters and maximum  10")
	private String cellphoneNumber;
	private String origin;

}
