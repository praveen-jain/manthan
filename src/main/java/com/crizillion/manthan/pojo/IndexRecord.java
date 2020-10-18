package com.crizillion.manthan.pojo;

import java.util.Date;

import org.apache.commons.lang3.Validate;

public class IndexRecord {
	
	private RecordType type;
	private Date date;
	private String stock;
	private String stockName;
	private String industry;
	private Double price;
	private Double mcap;
	private Double weight;
	private Month month;

	public IndexRecord(String stock, Date date) {
		Validate.notNull(date);
		Validate.notNull(stock);
		this.date = date;
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
	public Double getMcap() {
		return mcap;
	}
	public void setMcap(Double mcap) {
		this.mcap = mcap;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public String getStockName() {
		return stockName;
	}
	public void setStockName(String stockName) {
		this.stockName = stockName;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public RecordType getType() {
		return type;
	}
	public void setType(RecordType type) {
		this.type = type;
	}
	public Month getMonth() {
		return month;
	}
	public PriceData getPriceData() {
		return new PriceData(stock, date, price);
	}
	@Override
	public String toString() {
		return "MasterRecord [type=" + type + ", date=" + date + ", stock=" + stock + ", price=" + price + ", weight="+ weight + "]";
	}
	
}
