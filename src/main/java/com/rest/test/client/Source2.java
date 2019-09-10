package com.rest.test.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.*;
import java.io.Writer;

//Source 2: Responds to GET requests at https://gitter.com:9000/ and stores them in the DB via RESTDataService

public class Source2 {
	
	public static void main(String[] args) {
	    try {
	    	//GET
			System.out.println("Getting from https://gitter.com:9000 ...");
			LocalDateTime end  = LocalDateTime.now();
			LocalDateTime start = end.minusMinutes(60);
			String startTime = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(start);
			String endTime = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(end);
			URL url = new URL("https://gitter.com:9000/?start="+startTime+"&end="+endTime);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
			conn.disconnect();
	    } catch (MalformedURLException e) {
	    	e.printStackTrace();
	    } catch (IOException e) {
			e.printStackTrace();
	    } catch (Exception e) {
			e.printStackTrace();
	    }
	} 
}
