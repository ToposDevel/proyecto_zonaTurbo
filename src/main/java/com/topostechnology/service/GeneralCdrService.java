package com.topostechnology.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.topostechnology.model.MonthModel;

public class  GeneralCdrService {
	
	public static LocalDateTime getInitMonthDateTime(String monthYear) {
		String[] parts = monthYear.split("-");
		String month = parts[0]; 
		String year = parts[1]; 
		int initDay = 1;
		LocalDate initDate = LocalDate.of(Integer.valueOf(year), Integer.valueOf(month), initDay);
		LocalDateTime initDateTime = initDate.atTime(LocalTime.MIN);
		return initDateTime;
	}
	
	public static LocalDateTime getEndMonthDateTime(LocalDate initDate) {
		LocalDate endDate = initDate.with(TemporalAdjusters.lastDayOfMonth());
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
		return endDateTime;
	}
	
	public static List<MonthModel> getLastMonths(int numberLastMonths) {
		List<MonthModel> lastMonthList = new ArrayList<MonthModel>();
		for(int i = 1; i <= numberLastMonths;  i++) {
			LocalDate todayLocalDate = LocalDate.now();
			LocalDate monthDate = todayLocalDate.minus(i, ChronoUnit.MONTHS);
			Month mes = monthDate.getMonth();
			int year = monthDate.getYear();
			String  monthYear = "" +  mes.getValue() + "-" +  year ;
			 String monthYearStr = "" + mes.getDisplayName(TextStyle.FULL, new Locale("es", "ES")) + "-" +  year;
			MonthModel monthModel = new MonthModel(monthYear, monthYearStr);
			lastMonthList.add(monthModel);
		}
		return lastMonthList;
	}
	

}
