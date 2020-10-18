package com.crizillion.manthan.master;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.web.client.RestTemplate;

public class IndexDataFetcher {
	
	public static void main(String[] args) throws Exception{
		Date d = DateUtils.addMonths(new Date(), -1);
		System.out.println(d);
		for(int i=0;i<84;i++) {
			String month = FastDateFormat.getInstance("MMMyy").format(DateUtils.addMonths(d, -i)).toLowerCase();
			try {
				System.out.println("Going to fetch data for month: "+month);
				byte[] response = new RestTemplate().getForObject(new URI("https://www1.nseindia.com/content/indices/mcwb_"+month+".zip"), byte[].class);
				ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(response));
				FileUtils.writeByteArrayToFile(new File("masters/index/"+month+".zip"), response);
				ZipEntry zipEntry = null;
				while ((zipEntry = zis.getNextEntry()) != null) {
					byte [] entry = zis.readAllBytes();
					if(zipEntry.getName().contains("jr") || zipEntry.getName().contains("next")) {
						FileUtils.writeByteArrayToFile(new File("masters/next50/"+month+".csv"), entry);
					}else {
						FileUtils.writeByteArrayToFile(new File("masters/nifty/"+month+".csv"), entry);
					}
		        }	
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
