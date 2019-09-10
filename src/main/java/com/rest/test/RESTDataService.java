package com.rest.test;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import com.mongodb.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.Date;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Path("/data")
public class RESTDataService {
	
	//Source 1: This method accepts the POST requests from source 1 and persists the data
	@POST
	@Consumes("application/json")
	public int saveSource1Data(String data) {
		int responseCode = HttpURLConnection.HTTP_ACCEPTED;
		try {
			MongoClient mongoClient = MongoClients.create();
			MongoDatabase database = mongoClient.getDatabase("restdata");
			MongoCollection<Document> collection = database.getCollection("source1");
			Document document = new Document(Document.parse(data));
			collection.insertOne(document);
			responseCode = HttpURLConnection.HTTP_OK;
		} catch (Exception ex) {
			System.out.println(ex);
		} finally {
			return responseCode;
		}
	}

	//Source 2: Responds to GET requests at https://gitter.com:9000/ and stores them in the DB
	//@GET
	//@Produces("text/plain")
	public void saveSource2Data() {
		ScheduledExecutorService executor =
			    Executors.newSingleThreadScheduledExecutor();
			Runnable periodicTask = new Runnable() {
			    public void run() {
					try {
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
						
						MongoClient mongoClient = MongoClients.create();
						MongoDatabase database = mongoClient.getDatabase("restdata");
						MongoCollection<Document> collection = database.getCollection("source1");
						Document document = new Document("name", "Café Con Leche")
					               .append("contact", new Document("phone", "228-555-0149")
					                                       .append("email", "cafeconleche@example.com")
					                                       .append("location",Arrays.asList(-73.92502, 40.8279556)))
					               .append("stars", 3)
					               .append("categories", Arrays.asList("Bakery", "Coffee", "Pastries"));

						System.out.println("Inserting... "+document.toString());
						collection.insertOne(document);
					} catch (Exception ex) {
						System.out.println(ex);
					}
					}
			};
		executor.scheduleAtFixedRate(periodicTask, 0, 60, TimeUnit.MINUTES);
	}
}
