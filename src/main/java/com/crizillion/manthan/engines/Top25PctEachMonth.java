package com.crizillion.manthan.engines;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crizillion.manthan.infra.Loggers;
import com.crizillion.manthan.infra.Utils;
import com.crizillion.manthan.master.BhavService;
import com.crizillion.manthan.master.IndexService;
import com.crizillion.manthan.pojo.BhavRecord;
import com.crizillion.manthan.pojo.IndexRecord;
import com.crizillion.manthan.pojo.Month;
import com.crizillion.manthan.pojo.RecordType;
import com.crizillion.manthan.pojo.TransactionRecord;
import com.crizillion.manthan.transaction.AccountingService;
import com.crizillion.manthan.transaction.StockSummary;
import com.crizillion.manthan.transaction.TransactionService;

@Service
public class Top25PctEachMonth implements AlgorithmExecutor{
	
	private final IndexService indexService;
	private final TransactionService transactionService;
	private final AccountingService accountingService;
	private final BhavService bhavService;
	private List<TransactionRecord> transactions = new ArrayList<>();
	private Month currentMonth;

	public Top25PctEachMonth(IndexService indexService, TransactionService transactionService,
							 AccountingService accountingService, BhavService bhavService) {
		this.indexService = indexService;
		this.transactionService = transactionService;
		this.accountingService = accountingService;
		this.bhavService = bhavService;
	}

	public void execute(){
		List<Month> months = new ArrayList<>(indexService.getMonths());
		months.stream().forEach(m -> executeMonth(m));
		emitCSV("target/"+getClass().getSimpleName()+"_"+new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+".csv");
	}

	private void executeMonth(Month month) {
		accountingService.addSIPAmount(10000d);
		currentMonth = month;
		
		//Sell logic
		Map<String, List<TransactionRecord>> transactionGroups = new HashMap<>();
		transactions.stream().forEach(t ->{
			transactionGroups.putIfAbsent(t.getStock(), new ArrayList<>());
			transactionGroups.get(t.getStock()).add(t);
		});
		for(Entry<String, List<TransactionRecord>> entry : transactionGroups.entrySet()) {
			BhavRecord firstDayPriceData = bhavService.getFirstDayData(entry.getKey(), currentMonth);
			if(firstDayPriceData == null) {
				Loggers.transactions.error("No price data found for {} for the month {}", entry.getKey(), currentMonth);
				continue;
			}
			StockSummary summary = transactionService.getSummary(entry.getValue(), firstDayPriceData.getPriceData());
			if(summary.getStock().equals("TECHM")) {
				System.out.println("HERE");
			}
			if(summary.getInvestedQuantity() > 0 && summary.getPlPercent() <= -25) {
				Loggers.transactions.info("Selling {} quantity of {} at price {} on {} due to 25% aggregate LOSS", summary.getInvestedQuantity(), summary.getStock(),
						firstDayPriceData.getClose(), firstDayPriceData.getDate());
				TransactionRecord sellTransaction = transactionService.sell(firstDayPriceData.getPriceData(), summary.getInvestedQuantity());
				sellTransaction.setComment("Sold due to 25% aggregate loss");
				this.transactions.add(sellTransaction);
			}			
		}
	
		//Buy logic
		List<IndexRecord> thisMonthsStocks = indexService.getIndexRecords(m -> m.getMonth().equals(month) && m.getType() == RecordType.NEXT50);
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
			TransactionRecord buyTransaction = transactionService.buy(stock.getPriceData(), quantity);
			buyTransaction.setAllocationPct(percentAllocation);
			transactions.add(buyTransaction);
		}
	}
	
	public void emitCSV(String fileLocation) throws RuntimeException{
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileLocation));
				CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("STOCK", "TYPE", "DATE", "PRICE", "ALLOCATION_PCT" , "QUANTITY", "AMOUNT"
						, "VALUE_ON_"+currentMonth.getValue()));) {
			for(TransactionRecord t : transactions) {
				BhavRecord currentMonthPrice = bhavService.getFirstDayData(t.getStock(), currentMonth);
				String currentMonthAmount = currentMonthPrice != null ? Utils.format(currentMonthPrice.getClose() * t.getQuantity()) : "0";
				csvPrinter.printRecord(t.getStock(), t.getType(), Utils.formatDate(t.getDate()), Utils.format(t.getPrice())
						, t.getAllocationPct() != null ? 0 : Utils.format(t.getAllocationPct())
						, Utils.format(t.getQuantity()), Utils.format(t.getAmount()), currentMonthAmount);	
			}
			csvPrinter.flush();         
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
