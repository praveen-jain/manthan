package com.crizillion.manthan.transaction;

import org.springframework.stereotype.Service;

import com.crizillion.manthan.infra.Loggers;
import com.crizillion.manthan.pojo.TransactionRecord;

@Service
public class AccountingServiceImpl implements AccountingService{
	
	private Double accountBalance = Double.valueOf(0);
	
	@Override
	public void reset() {
		Loggers.accounts.info("Resetting account balance to ZERO");		
	}
	
	public void addSIPAmount(Double amount) {
		accountBalance += amount;
		Loggers.accounts.info("Added Rs {}into account as SIP for the month. New balance {}", amount, accountBalance);
	}
	
	@Override
	public void sell(TransactionRecord transaction) {
		Double previousBalance = accountBalance;
		accountBalance += Math.abs(transaction.getAmount());
		Loggers.accounts.info("Added Rs {} into account due to selling of {} quantity of {} on {}. Previous Balance: {}, New balance: {}"
				, transaction.getAmount(), transaction.getStock(), transaction.getQuantity(), transaction.getDate(), previousBalance, accountBalance);
	}
	
	@Override
	public void buy(TransactionRecord transaction) {
		if(accountBalance < transaction.getAmount()-0.1) {	//10 paisa rounding error allowed
			throw new RuntimeException("Insufficient balance ("+accountBalance+") for transaction: "+transaction);
		}
		Double previousBalance = accountBalance;
		accountBalance -= Math.abs(transaction.getAmount());
		if(accountBalance < 0) {
			accountBalance = 0d;		//10 paisa rounding error allowed
		}
		Loggers.accounts.info("Deducted Rs {} from account due to buying of {} quantity of {} on {}. Previous Balance: {}, New balance: {}"
				, transaction.getAmount(), transaction.getStock(), transaction.getQuantity(), transaction.getDate(), previousBalance, accountBalance);
	}
	
	@Override
	public Double getBalance() {
		return accountBalance;
	}
	
	
}
