package com.crizillion.manthan.pojo;

import java.util.Date;

import org.apache.commons.lang3.Validate;

public class PriceData {
	
	private Date date;
	private String stock;
	private Double price;
	private Month month;

	public PriceData(String stock, Date date, Double price) {
		Validate.notNull(date);
		Validate.notNull(stock);
		Validate.notNull(price);
		this.date = date;
		this.price = price;
		this.stock = stock;
		this.month = new Month(date);
	}
	public String getStock() {
		return stock;
	}
	public Date getDate() {
		return date;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Month getMonth() {
		return month;
	}
	@Override
	public String toString() {
		return "PriceData [date=" + date + ", stock=" + stock + ", price=" + price + ", month=" + month + "]";
	}
}
