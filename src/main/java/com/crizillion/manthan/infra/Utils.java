package com.crizillion.manthan.infra;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	
	public static Date parseDate(String input) {
		try {
			return new SimpleDateFormat("MM/dd/yyyy").parse(input);
		} catch (ParseException e) {
			throw new RuntimeException("Could not parse date "+input, e);
		}
	}
	
	public static String formatDate(Date input) {
		return new SimpleDateFormat("MM/dd/yyyy").format(input);
	}
	
	public static String format(Double input) {
		return String.format("%.2f", input);
	}

}
