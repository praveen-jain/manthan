package com.crizillion.manthan.engines;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.crizillion.manthan.infra.Loggers;
import com.crizillion.manthan.transaction.AccountingService;
import com.crizillion.manthan.transaction.TransactionService;

@Component
public class AnalysisEngine {
	
	@Autowired private List<AlgorithmExecutor> executors;
	@Autowired private AccountingService accountingService;
	@Autowired private TransactionService transactionService;
	
	@EventListener(classes = ContextRefreshedEvent.class)
	public void run() {
		for(AlgorithmExecutor executor : executors) {
			Loggers.engine.info("Running executor : {}", executor.getClass().getName());
			accountingService.reset();
			transactionService.reset();
			executor.execute();
			transactionService.emitCSV("target/"+new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+".csv");
		}
	}

}
