package com.topostechnology.model;

import java.util.List;

import com.topostechnology.dto.CoverageDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateCoverageModel {
	
	private String address;
	private String zipcode;
	private Long latitude;
	private Long longitude;
	private String coordinates;
	private String callcenterPhone;
	private String coverageMessage = "";
//	private List<String> coverageValidationMessages ;
	List<CoverageDto> coverageList;
	private String foundAddress = "";
	private String origin;
	
}
