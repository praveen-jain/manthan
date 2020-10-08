package com.crizillion.manthan.transaction;

import com.crizillion.manthan.pojo.TransactionRecord;

public interface AccountingService {
	
	public void addSIPAmount(Double amount);

	public void sell(TransactionRecord transaction);

	public void buy(TransactionRecord transaction);
	
	public Double getBalance();
	
	public void reset();
	
}
