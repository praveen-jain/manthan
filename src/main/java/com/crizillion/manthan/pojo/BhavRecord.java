package com.crizillion.manthan.pojo;

import java.util.Date;

import org.apache.commons.lang3.Validate;

public class BhavRecord {
	
	private RecordType type = RecordType.BHAV;
	private Date date;
	private String stock;
	private String series;
	private Double open;
	private Double high;
	private Double low;
	private Double close;
	private Month month;
	
	public BhavRecord(Date date, String stock) {
		Validate.notNull(date);
		Validate.notNull(stock);
		this.date = date;
		this.month = new Month(this.date);
		this.stock = stock;
	}
	public RecordType getType() {
		return type;
	}
	public void setType(RecordType type) {
		this.type = type;
	}
	public Date getDate() {
		return date;
	}
	public String getStock() {
		return stock;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public Double getOpen() {
		return open;
	}
	public void setOpen(Double open) {
		this.open = open;
	}
	public Double getHigh() {
		return high;
	}
	public void setHigh(Double high) {
		this.high = high;
	}
	public Double getLow() {
		return low;
	}
	public void setLow(Double low) {
		this.low = low;
	}
	public Double getClose() {
		return close;
	}
	public void setClose(Double close) {
		this.close = close;
	}
	public Month getMonth() {
		return month;
	}
	public PriceData getPriceData() {
		return new PriceData(stock, date, close);
	}
	@Override
	public String toString() {
		return "BhavRecord [date=" + date + ", stock=" + stock + ", close=" + close + "]";
	}
}
