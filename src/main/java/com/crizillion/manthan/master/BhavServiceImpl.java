package com.crizillion.manthan.master;

import java.io.File;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.stereotype.Service;

import com.crizillion.manthan.infra.Loggers;
import com.crizillion.manthan.pojo.BhavRecord;
import com.crizillion.manthan.pojo.Month;

@Service
public class BhavServiceImpl implements BhavService{
	
	private Map<String, List<BhavRecord>> bhavRecords = new LinkedHashMap<>();
	
	@PostConstruct
	public void init() throws Exception{
		Iterator<File> fileIterator = FileUtils.iterateFiles(new File("masters/bhav"), new String[] {"csv"}, true);
		File f = null;
		while(fileIterator.hasNext()) {
			try {
				f = fileIterator.next();
				Loggers.masters.info("Reading records from file: {}", f.getName());
				List<String> records = FileUtils.readLines(f, Charset.defaultCharset());
				parseRecords(records);
			}catch(Exception e) {
				Loggers.masters.error("Error while reading file: "+f.getName(), e);
			}
		}
	}

	private void parseRecords(List<String> records) {
		for (int i = 0; i < records.size(); i++) {
			if(i == 0) {
				continue;
			}
			String [] splittedParts = records.get(i).split(",");
			if(!splittedParts[1].equals("EQ") || splittedParts[1].equals("BE")) {
				continue;
			}
			BhavRecord m = null;
			try {
				Date date = FastDateFormat.getInstance("dd-MMM-yyyy").parse(splittedParts[10]);
				m = new BhavRecord(date, splittedParts[0]);
				m.setClose(Double.valueOf(splittedParts[5]));
			} catch (ParseException e) {
				throw new RuntimeException("Could not parse date in record: "+records.get(i));
			}
			if(!bhavRecords.containsKey(m.getStock())) {
				bhavRecords.put(m.getStock(), new ArrayList<>());
			}
			bhavRecords.get(m.getStock()).add(m);
		}
	}
	
	public BhavRecord getFirstDayData(String script, Month month) {
		List<BhavRecord> monthRecords = getBhavRecords(script, (b) -> b.getMonth().equals(month), (b1,b2) -> b1.getDate().compareTo(b2.getDate()));
		return monthRecords.isEmpty() ? null : monthRecords.get(0);
	}
	
	@Override
	public List<BhavRecord> getBhavRecords(String script, Predicate<BhavRecord> predicate) {
		List<BhavRecord> records = bhavRecords.get(script);
		Validate.notNull(records,"Bhav records not found for script : "+script);
		return records.stream().filter(predicate).collect(Collectors.toList());
	}
	
	@Override
	public List<BhavRecord> getBhavRecords(String script, Predicate<BhavRecord> predicate, Comparator<BhavRecord> sorter) {
		List<BhavRecord> records = getBhavRecords(script, predicate);
		records.sort(sorter);
		return records;
	}
	
}
