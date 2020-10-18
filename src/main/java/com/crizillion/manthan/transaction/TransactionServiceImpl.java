package com.crizillion.manthan.transaction;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crizillion.manthan.pojo.PriceData;
import com.crizillion.manthan.pojo.TransactionRecord;

@Service
public class TransactionServiceImpl implements TransactionService{

	@Autowired private AccountingService accountingService;

	@Override
	public StockSummary getSummary(List<TransactionRecord> previousTransactions, PriceData priceData) {
		StockSummary summary = new StockSummary();
		previousTransactions.stream().forEach(t -> {
			summary.setStock(t.getStock());
			summary.setInvestedQuantity(summary.getInvestedQuantity() + t.getQuantity());
			summary.setInvestedAmount(summary.getInvestedAmount() + t.getAmount());
			if(t.getQuantity() > 0) {
				summary.setTotalBuys(summary.getTotalBuys() + 1);
			}else {
				summary.setTotalSells(summary.getTotalSells() + 1);
			}
			summary.setCurrentValue(summary.getInvestedQuantity() * priceData.getPrice());
			summary.setPlAmount(summary.getCurrentValue() - summary.getInvestedAmount());
			summary.setPlPercent((summary.getCurrentValue() - summary.getInvestedAmount())/summary.getInvestedAmount() * 100);
		});
		return summary;
	}

	@Override
	public TransactionRecord sell(PriceData priceData, Double quantity) {
		TransactionRecord transaction = new TransactionRecord();
		transaction.setQuantity(Math.abs(quantity) * -1);
		transaction.setDate(priceData.getDate());
		transaction.setPrice(priceData.getPrice());
		transaction.setStock(priceData.getStock());
		accountingService.sell(transaction);
		return transaction;
	}

	@Override
	public TransactionRecord buy(PriceData priceData, Double quantity) {
		TransactionRecord transaction = new TransactionRecord();
		transaction.setQuantity(Math.abs(quantity));
		transaction.setDate(priceData.getDate());
		transaction.setPrice(priceData.getPrice());
		transaction.setStock(priceData.getStock());
		accountingService.buy(transaction);
		return transaction;
	}

}
