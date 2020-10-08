package com.crizillion.manthan.transaction;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crizillion.manthan.infra.Loggers;
import com.crizillion.manthan.infra.Utils;
import com.crizillion.manthan.master.IndexService;
import com.crizillion.manthan.pojo.IndexRecord;
import com.crizillion.manthan.pojo.Month;
import com.crizillion.manthan.pojo.TransactionRecord;

@Service
public class TransactionServiceImpl implements TransactionService{

	@Autowired private AccountingService accountingService;
	@Autowired private IndexService masterService;
	private List<TransactionRecord> transactions = new ArrayList<>();
	private Month currentMonth;

	public List<TransactionRecord> getAllTransactions(Predicate<TransactionRecord> predicate){
		return transactions.stream().filter(predicate).collect(Collectors.toList());
	}

	@Override
	public StockSummary getSummary(List<TransactionRecord> previousTransactions, IndexRecord currentData) {
		StockSummary summary = new StockSummary();
		previousTransactions.stream().forEach(t -> {
			summary.setStock(t.getStock());
			summary.setInvestedQuantity(summary.getInvestedQuantity() + t.getQuantity());
			summary.setInvestedAmount(summary.getInvestedAmount() + (t.getAmount()));
			if(t.getQuantity() > 0) {
				summary.setTotalBuys(summary.getTotalBuys() + 1);
			}else {
				summary.setTotalSells(summary.getTotalSells() + 1);
			}
			summary.setCurrentValue(summary.getInvestedQuantity() * currentData.getPrice());
			summary.setPlAmount(summary.getCurrentValue() - summary.getInvestedAmount());
			summary.setPlPercent(summary.getCurrentValue()/summary.getInvestedAmount() * 100);

		});
		return summary;
	}

	@Override
	public void sell(IndexRecord masterRecord, Double quantity, String comment) {
		TransactionRecord transaction = new TransactionRecord();
		transaction.setQuantity(Math.abs(quantity) * -1);
		transaction.setDate(masterRecord.getDate());
		transaction.setPrice(masterRecord.getPrice());
		transaction.setComment(comment);
		transaction.setStock(masterRecord.getStock());
		accountingService.sell(transaction);
		transactions.add(transaction);
	}

	@Override
	public void buy(IndexRecord masterRecord, Double quantity) {
		TransactionRecord transaction = new TransactionRecord();
		transaction.setQuantity(Math.abs(quantity));
		transaction.setDate(masterRecord.getDate());
		transaction.setPrice(masterRecord.getPrice());
		transaction.setStock(masterRecord.getStock());
		accountingService.buy(transaction);
		transactions.add(transaction);
	}

	@Override
	public void reset() {
		transactions.clear();
		currentMonth = null;
		Loggers.transactions.info("All transactions cleared");
	}

	@Override
	public void emitCSV(String fileLocation) throws RuntimeException{
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileLocation));
				CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("STOCK", "TYPE", "DATE", "PRICE", "QUANTITY", "AMOUNT"
						, "VALUE_ON_"+currentMonth.getValue()));) {
			for(TransactionRecord t : transactions) {
				List<IndexRecord> currentMonthData = masterService.getIndexRecords(m -> m.getMonth().equals(currentMonth) && m.getStock().equals(t.getStock()));
				String currentMonthAmount = currentMonthData.size() > 0 ? Utils.format(currentMonthData.get(0).getPrice() * t.getQuantity()) : "-";
				csvPrinter.printRecord(t.getStock(), t.getType(), Utils.formatDate(t.getDate()), Utils.format(t.getPrice())
						, Utils.format(t.getQuantity()), Utils.format(t.getAmount()), currentMonthAmount);	
			}
			csvPrinter.flush();         
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void setCurrentMonth(Month month) {
		this.currentMonth = month;
	}
}
