package com.crizillion.manthan.pojo;

import java.util.Date;

public class TransactionRecord {
	
	private Date date;
	private String stock;
	private Double price;
	private Double quantity;
	private Double allocationPct;
	private String comment;
	private Double accountBalanceBeforeTransaction;
	private Double accountBalanceAfterTransaction;
	
	public String getStock() {
		return stock;
	}
	public void setStock(String stock) {
		this.stock = stock;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Month getMonth() {
		return new Month(date);
	}
	public TransactionType getType() {
		return quantity > 0 ? TransactionType.BUY : TransactionType.SELL;
	}
	public Double getAmount() {
		return this.quantity * this.price;
	}
	public Double getAllocationPct() {
		return allocationPct;
	}
	public void setAllocationPct(Double allocationPct) {
		this.allocationPct = allocationPct;
	}
	public Double getAccountBalanceBeforeTransaction() {
		return accountBalanceBeforeTransaction;
	}
	public void setAccountBalanceBeforeTransaction(Double accountBalanceBeforeTransaction) {
		this.accountBalanceBeforeTransaction = accountBalanceBeforeTransaction;
	}
	public Double getAccountBalanceAfterTransaction() {
		return accountBalanceAfterTransaction;
	}
	public void setAccountBalanceAfterTransaction(Double accountBalanceAfterTransaction) {
		this.accountBalanceAfterTransaction = accountBalanceAfterTransaction;
	}
	@Override
	public String toString() {
		return "TransactionRecord [date=" + date + ", stock=" + stock + ", price=" + price + ", quantity=" + quantity+ "]";
	}
	
	
}
