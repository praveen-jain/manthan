package com.crizillion.manthan.master;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import com.crizillion.manthan.infra.Loggers;

public class BhavCopyFetcher {
	
	public static void main(String[] args) throws Exception{
		Date startDate = FastDateFormat.getInstance("ddMMyyyy").parse("01012016");
		for(int i=0;i<1000; i++) {
			Date date = DateUtils.addDays(startDate, -i);
			if(date.getDay() == 0 || date.getDay() == 6) {
				continue;
			}
			String dateString = FastDateFormat.getInstance("ddMMMyyyy").format(date).toUpperCase();
			try {
				Loggers.masters.info("Going to fetch bhavcopy data for date: "+dateString);
				String url = String.format("https://www1.nseindia.com/content/historical/EQUITIES/%s/%s/cm%sbhav.csv.zip", dateString.substring(5), dateString.substring(2,5), dateString);
				Loggers.masters.info(url);
				
				HttpHeaders headers = new HttpHeaders();
				headers.add("Referer", "https://www1.nseindia.com/products/content/equities/equities/archieve_eq.htm");

				RequestEntity<?> entity = RequestEntity.get(new URI(url)).headers(headers).build();
				
				byte[] response = new RestTemplate().exchange(entity, byte[].class).getBody();
				ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(response));
				while ((zis.getNextEntry()) != null) {
					byte [] entry = zis.readAllBytes();
					FileUtils.writeByteArrayToFile(new File("masters/bhav/"+FastDateFormat.getInstance("yyyyMMdd").format(date)+".csv"), entry);
		        }	
			}catch(Exception e) {
				Loggers.masters.error("Exception while fetching bhavcopy date for date "+dateString);;
			}
		}
	}

}
