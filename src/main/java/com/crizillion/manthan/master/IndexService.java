package com.crizillion.manthan.master;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import com.crizillion.manthan.pojo.IndexRecord;
import com.crizillion.manthan.pojo.Month;

public interface IndexService {
	
	public Collection<Month> getMonths();
	
	public List<IndexRecord> getIndexRecords(Predicate<IndexRecord> predicate);

}