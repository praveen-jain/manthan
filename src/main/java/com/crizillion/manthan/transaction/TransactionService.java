package com.crizillion.manthan.transaction;

import java.util.List;
import java.util.function.Predicate;

import com.crizillion.manthan.pojo.IndexRecord;
import com.crizillion.manthan.pojo.Month;
import com.crizillion.manthan.pojo.TransactionRecord;

public interface TransactionService {
	
	public List<TransactionRecord> getAllTransactions(Predicate<TransactionRecord> predicate);
	
	public StockSummary getSummary(List<TransactionRecord> previousTransactions, IndexRecord currentData);

	public void sell(IndexRecord masterRecord, Double quantity, String comment);

	public void buy(IndexRecord stock, Double quantity);
	
	public void reset();

	public void emitCSV(String fileLocation);

	public void setCurrentMonth(Month month);

}
