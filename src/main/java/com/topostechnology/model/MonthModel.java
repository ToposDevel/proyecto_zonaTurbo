package com.topostechnology.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthModel {
	
	private String  monthYear;
	private String monthYearStr;
	
	public MonthModel(String monthYear, String monthYearStr ) {
		this.monthYear = monthYear;
		this.monthYearStr = monthYearStr;
	}

}
