package com.crizillion.manthan.master;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.stereotype.Service;

import com.crizillion.manthan.infra.Loggers;
import com.crizillion.manthan.pojo.IndexRecord;
import com.crizillion.manthan.pojo.Month;
import com.crizillion.manthan.pojo.RecordType;

@Service
public class IndexServiceImpl implements IndexService{
	
	private Set<Month> months = new TreeSet<>();
	private List<IndexRecord> masterRecords = new ArrayList<>();
	
	@PostConstruct
	public void init() throws Exception{
		Iterator<File> fileIterator = FileUtils.iterateFiles(new File("masters"), new String[] {"csv"}, true);
		File f = null;
		while(fileIterator.hasNext()) {
			try {
				f = fileIterator.next();
				if(f.getPath().contains("bhav")) {
					continue;
				}
				RecordType recordType = f.getPath().contains("nifty") ? RecordType.NIFTY : RecordType.NEXT50;
				Date date = FastDateFormat.getInstance("MMMyy").parse(StringUtils.capitalize(f.getName().substring(0, f.getName().indexOf("."))));
				Loggers.masters.info("Reading records from file: {}", f.getName());
				List<String> records = FileUtils.readLines(f, Charset.defaultCharset());
				parseMasterRecords(records, recordType, date);	
			}catch(Exception e) {
				Loggers.masters.error("Error while reading file: "+f.getName(), e);
			}
		}
		setupMonthsData();
	}

	private void parseMasterRecords(List<String> records, RecordType recordType, Date date) {
		List<IndexRecord> parsedRecords = records.stream().filter(r -> r.matches("^(\\d\\d?).*$")).map(r -> {
			String [] splittedParts = r.split(",");
			IndexRecord m = new IndexRecord();
			m.setDate(date);
			m.setType(recordType);
			m.setStock(splittedParts[1]);
			m.setStockName(splittedParts[2]);
			m.setIndustry(splittedParts[3]);
			//m.setPrice(Double.valueOf(splittedParts[5]));
			m.setWeight(Double.valueOf(splittedParts[6]));
			return m;
		}).collect(Collectors.toList());
		
		masterRecords.addAll(parsedRecords);
	}
	
	private void setupMonthsData() {
		masterRecords.stream().map(r -> r.getDate()).forEach(r -> months.add(new Month((Date)r)));
		months = Collections.unmodifiableSet(months);
		Loggers.masters.info("Generated the following months {}", months);
	}
	
	@Override
	public Collection<Month> getMonths() {
		return months;
	}
	
	@Override
	public List<IndexRecord> getIndexRecords(Predicate<IndexRecord> predicate) {
		return masterRecords.stream().filter(predicate).collect(Collectors.toList());
	}
	
}
