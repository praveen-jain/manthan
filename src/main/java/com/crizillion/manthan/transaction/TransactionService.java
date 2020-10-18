package com.crizillion.manthan.transaction;

import java.util.List;

import com.crizillion.manthan.pojo.PriceData;
import com.crizillion.manthan.pojo.TransactionRecord;

public interface TransactionService {
	
	public StockSummary getSummary(List<TransactionRecord> previousTransactions, PriceData priceData);

	public TransactionRecord sell(PriceData priceData, Double quantity);

	public TransactionRecord buy(PriceData priceData, Double quantity);
	
}
