package com.crizillion.manthan.master;

import java.io.File;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.stereotype.Service;

import com.crizillion.manthan.infra.Loggers;
import com.crizillion.manthan.pojo.BhavRecord;

@Service
public class BhavServiceImpl implements BhavService{
	
	private List<BhavRecord> bhavRecords = new ArrayList<>();
	
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
			if(!splittedParts[1].equals("EQ")) {
				continue;
			}
			BhavRecord m = new BhavRecord();
			m.setStock(splittedParts[0]);
			m.setClose(Double.valueOf(splittedParts[5]));
			try {
				m.setDate(FastDateFormat.getInstance("dd-MMM-yyyy").parse(splittedParts[10]));
			} catch (ParseException e) {
				Loggers.masters.info("Could not parse date {}", splittedParts[10]);
			}
			bhavRecords.add(m);
		}
	}
	
	@Override
	public List<BhavRecord> getBhavRecords(Predicate<BhavRecord> predicate) {
		return bhavRecords.stream().filter(predicate).collect(Collectors.toList());
	}
	
}
