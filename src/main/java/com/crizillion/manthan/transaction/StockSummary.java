package com.crizillion.manthan.transaction;

public class StockSummary {
	
	private String stock;
	private Double investedQuantity = Double.valueOf(0);
	private Double investedAmount = Double.valueOf(0);
	private Double currentValue = Double.valueOf(0);
	private Double plAmount = Double.valueOf(0);
	private Double plPercent = Double.valueOf(0);
	private int totalBuys;
	private int totalSells;
	public Double getInvestedQuantity() {
		return investedQuantity;
	}
	public void setInvestedQuantity(Double investedQuantity) {
		this.investedQuantity = investedQuantity;
	}
	public Double getInvestedAmount() {
		return investedAmount;
	}
	public void setInvestedAmount(Double investedAmount) {
		this.investedAmount = investedAmount;
	}
	public Double getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(Double currentValue) {
		this.currentValue = currentValue;
	}
	public Double getPlAmount() {
		return plAmount;
	}
	public void setPlAmount(Double plAmount) {
		this.plAmount = plAmount;
	}
	public Double getPlPercent() {
		return plPercent;
	}
	public void setPlPercent(Double plPercent) {
		this.plPercent = plPercent;
	}
	public int getTotalBuys() {
		return totalBuys;
	}
	public void setTotalBuys(int totalBuys) {
		this.totalBuys = totalBuys;
	}
	public int getTotalSells() {
		return totalSells;
	}
	public void setTotalSells(int totalSells) {
		this.totalSells = totalSells;
	}
	public String getStock() {
		return stock;
	}
	public void setStock(String stock) {
		this.stock = stock;
	}
}
