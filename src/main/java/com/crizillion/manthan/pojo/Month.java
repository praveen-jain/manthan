package com.crizillion.manthan.pojo;

import java.util.Date;

public class Month implements Comparable<Month>{
	
	private String value;
	private Date date;
	
	public Month(Date date) {
		this.date = date;
		this.value = String.valueOf(String.format("%02d", (date.getMonth()+1)) + (date.getYear()+1900));
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Month other = (Month) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Month [month=" + value+"]";
	}
	
	@Override
	public int compareTo(Month o) {
		return this.date.compareTo(o.date);
	}
	
}