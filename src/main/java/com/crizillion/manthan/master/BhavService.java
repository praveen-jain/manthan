package com.crizillion.manthan.master;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import com.crizillion.manthan.pojo.BhavRecord;
import com.crizillion.manthan.pojo.Month;

public interface BhavService {
	
	public List<BhavRecord> getBhavRecords(String script, Predicate<BhavRecord> predicate);
	
	public List<BhavRecord> getBhavRecords(String script, Predicate<BhavRecord> predicate, Comparator<BhavRecord> sorter);
	
	public BhavRecord getFirstDayData(String script, Month month);

}