package com.crizillion.manthan.engines;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.crizillion.manthan.infra.Loggers;
import com.crizillion.manthan.transaction.AccountingService;

@Component
public class AnalysisEngine {
	
	@Autowired private List<AlgorithmExecutor> executors;
	@Autowired private AccountingService accountingService;
	
	@EventListener(classes = ContextRefreshedEvent.class)
	public void run() {
		for(AlgorithmExecutor executor : executors) {
			Loggers.engine.info("Running executor : {}", executor.getClass().getName());
			accountingService.reset();
			executor.execute();
		}
	}

}
