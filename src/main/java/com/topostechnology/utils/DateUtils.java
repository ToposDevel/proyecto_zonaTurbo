package com.topostechnology.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public final class DateUtils {

	public static final String DD_MM_YYY_HH_MM_SS = "dd/MM/yyyy HH:mm:ss";
	public static final String ISO_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
	private static final String DATE_MUST_NOT_BE_NULL = "Date must not be null";
	public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	public static final String YYYY_MM_DD_FORMAT = "yyyyMMdd";
	public static final String YYYYMMDD_FORMAT = "yyyy-MM-dd";

	private DateUtils() {

	}

	public static Date getDateFrom(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			throw new IllegalArgumentException("LocalDateTime must not be null");
		}
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date getCurrentDateTime() {
		return getDateFrom(LocalDateTime.now());
	}

	public static LocalDate getLocalDateFrom(Date date) {
		if (date == null) {
			throw new IllegalArgumentException(DATE_MUST_NOT_BE_NULL);
		}

		if (date instanceof java.sql.Date) {
			return ((java.sql.Date) date).toLocalDate();
		} else {
			Instant instant = date.toInstant();
			ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
			return zonedDateTime.toLocalDate();
		}
	}

	public static Date getInitDateTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date getEndDateTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}

	public static String getFormattedDate(Date date, String pattern) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		String format = formatter.format(date);
		return format;
	}

	public static Date addOrRemoveDays(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		return calendar.getTime();
	}

	public static Date stringToDate(String simpleDateFormat, String dateStr) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(simpleDateFormat);
		Date date = formatter.parse(dateStr);
		return date;
	}

	public static LocalDateTime getLocalDateTimeFrom(Date date) {
		if (date == null) {
			throw new IllegalArgumentException(DATE_MUST_NOT_BE_NULL);
		}

		Instant instant = date.toInstant();
		ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
		return zonedDateTime.toLocalDateTime();
	}

	public static String formatDate(Date date, String pattern) {
		if (date == null) {
			throw new IllegalArgumentException(DATE_MUST_NOT_BE_NULL);
		}

		if (pattern == null) {
			throw new IllegalArgumentException("Pattern must not be null");
		}

		return DateTimeFormatter.ofPattern(pattern).format(getLocalDateFrom(date));
	}

	public static String formatDate(LocalDate localDate, DateTimeFormatter formatter) {
		if (localDate == null) {
			throw new IllegalArgumentException(DATE_MUST_NOT_BE_NULL);
		}

		if (formatter == null) {
			throw new IllegalArgumentException("Formatter must not be null");
		}
		return formatter.format(localDate);
	}

	public static String formatDate(Date date, DateTimeFormatter formatter) {
		if (date == null) {
			throw new IllegalArgumentException(DATE_MUST_NOT_BE_NULL);
		}

		if (formatter == null) {
			throw new IllegalArgumentException("Pattern must not be null");
		}

		return formatter.format(getLocalDateFrom(date));
	}

	public static String formatDateTime(Date dateTime, DateTimeFormatter formatter) {
		if (dateTime == null) {
			throw new IllegalArgumentException("DateTime must not be null");
		}

		if (formatter == null) {
			throw new IllegalArgumentException("Formatter must not be null");
		}

		LocalDateTime localDateTime = getLocalDateTimeFrom(dateTime);
		return formatter.format(localDateTime);
	}
	public static String formatYYMMDD(String fecha) {
		
		if(!fecha.isEmpty()) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_FORMAT);
			LocalDate localDate = LocalDate.parse(fecha, formatter);
			
			ZoneId zoneIdMx = ZoneId.of("America/Mexico_City");
			ZonedDateTime zonedDateTime = localDate.atStartOfDay(zoneIdMx);
			
			return DateTimeFormatter.ofPattern(YYYYMMDD_FORMAT).format(zonedDateTime);
		}else {
			return "";
		}
	}
}
