package com.rest.test.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.*;
import java.io.Writer;

//Source 1: Makes a POST request every 15 minutes with the following example JSON object to RESTDataService

public class Source1 {
	
	public static void main(String[] args) {
	  try {
		  ScheduledExecutorService executor =
				    Executors.newSingleThreadScheduledExecutor();
				Runnable periodicTask = new Runnable() {
				    public void run() {
				    try {
						URL url = new URL("http://localhost:8080/com.rest.test/rest/data");
						System.out.print("Connecting to RESTDataService... " + url);
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						conn.setDoOutput(true);
						conn.setRequestMethod("POST");
						conn.setRequestProperty("Content-Type", "application/json");

						long timestamp = System.currentTimeMillis();
						
						JSONObject value = new JSONObject();
						JSONArray updates = new JSONArray();
						for (int i = 0; i < 100000; i++) {
							value.put("name", "Bob").put("git", "hello world!").put("timestamp", timestamp);
							updates.put(value);
						}
						
						value = new JSONObject();
						value.put("source", "gitters").put("updates", updates).put("timestamp", timestamp);
						
						OutputStream os = conn.getOutputStream();
						os.write(value.toString().getBytes());
						os.flush();
						//System.out.println(url);
						if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
							throw new RuntimeException("Failed : HTTP error code : "
								+ conn.getResponseCode());
						}

						BufferedReader br = new BufferedReader(new InputStreamReader(
								(conn.getInputStream())));

						String output;
						System.out.print("Output from RESTDataService... ");
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
			};
			executor.scheduleAtFixedRate(periodicTask, 0, 15, TimeUnit.MINUTES);
	    } catch (Exception e) {
			e.printStackTrace();
	    }
	} 
}
