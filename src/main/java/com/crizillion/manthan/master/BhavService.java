package com.crizillion.manthan.master;

import java.util.List;
import java.util.function.Predicate;

import com.crizillion.manthan.pojo.BhavRecord;

public interface BhavService {
	
	public List<BhavRecord> getBhavRecords(Predicate<BhavRecord> predicate);

}