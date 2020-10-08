package com.crizillion.manthan.engines;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crizillion.manthan.infra.Loggers;
import com.crizillion.manthan.master.IndexService;
import com.crizillion.manthan.pojo.IndexRecord;
import com.crizillion.manthan.pojo.Month;
import com.crizillion.manthan.pojo.TransactionRecord;
import com.crizillion.manthan.transaction.AccountingService;
import com.crizillion.manthan.transaction.StockSummary;
import com.crizillion.manthan.transaction.TransactionService;

@Service
public class Top25PctEachMonth implements AlgorithmExecutor{
	
	@Autowired private IndexService masterService;
	@Autowired private TransactionService transactionService;
	@Autowired private AccountingService accountingService;
	
	public void execute(){
		List<Month> months = new ArrayList<>(masterService.getMonths());
		months.stream().forEach(m -> executeMonth(m));
	}

	private void executeMonth(Month month) {
		accountingService.addSIPAmount(10000d);
		transactionService.setCurrentMonth(month);
		List<IndexRecord> thisMonthsStocks = masterService.getIndexRecords(m -> m.getMonth().equals(month));
		for(IndexRecord stock : thisMonthsStocks) {
			List<TransactionRecord> previousTransactions = transactionService.getAllTransactions(t -> t.getStock().equals(stock.getStock()));
			StockSummary summary = transactionService.getSummary(previousTransactions, stock);
			if(summary.getTotalBuys() != summary.getTotalSells() && summary.getPlPercent() <= -25) {
				Loggers.transactions.info("Selling {} quantity of {} at price {} on {} due to 25% aggregate LOSS", summary.getInvestedQuantity(), summary.getStock(),
						stock.getPrice(), stock.getDate());
				transactionService.sell(stock, summary.getInvestedQuantity(), "Sold due to 25% aggregate loss");
			}
		}
		
		final MutableDouble top25Pct = new MutableDouble();
		List<IndexRecord> toBuy = new ArrayList<>();
		thisMonthsStocks.stream().sorted((m1,m2) -> m2.getWeight().compareTo(m1.getWeight())).forEach(m -> {
			if(top25Pct.getValue() < 25) {
				top25Pct.add(m.getWeight());
				toBuy.add(m);
			}
		});
		
		Loggers.transactions.debug("Going to buy the following as Top 25% allotment: {}", toBuy);
		
		Double moneyToAllocate = accountingService.getBalance();
		for(IndexRecord stock : toBuy) {
			Double percentAllocation = stock.getWeight()/top25Pct.getValue()*100;
			//Quantity = amount to be invested / price = (account balance * allocation percent) / price
			Double quantity = moneyToAllocate * percentAllocation / 100 / stock.getPrice();
			Loggers.transactions.info("Buying {} quantity of {} at price {} on {} with {} allocation", quantity, stock.getStock(),
					stock.getPrice(), stock.getDate(), percentAllocation);
			transactionService.buy(stock, quantity);
		}
	}
	
}
