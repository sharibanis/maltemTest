package com.rest.test;

import java.net.HttpURLConnection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.*;

import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

//A user can query the endpoint with the following parameters: start, end
//Both parameters accept only datetimes of the format yyyyMMddhhmmss

@Path("/query")
public class RESTQueryService {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	//private ConsoleHandler consoleHandler;
	private FileHandler fileHandler;
	JSONObject results = new JSONObject();
	// This method services the query
	@GET
	@Produces("text/plain")
	//@Produces("application/json")
	public String query(@QueryParam("startDateTime") String startDateTime, @QueryParam("endDateTime") String endDateTime) {
		try {
			//consoleHandler = new ConsoleHandler();
			fileHandler = new FileHandler("logs/RESTQueryService.%u.%g.log");
			logger.addHandler(fileHandler);
			logger.log(Level.INFO, "Starting RESTQueryService. startDateTime is "+startDateTime 
					+ "; endDateTime is "+endDateTime);
			if ((startDateTime == null) || (endDateTime == null)) {
				throw new RuntimeException("startDateTime is "+startDateTime
						+ "; endDateTime is "+endDateTime);
			}
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			TemporalAccessor startTemporalAccessor = formatter.parse(startDateTime);
			TemporalAccessor endTemporalAccessor = formatter.parse(endDateTime);
		    LocalDateTime startLocalDateTime = LocalDateTime.from(startTemporalAccessor);
		    LocalDateTime endLocalDateTime = LocalDateTime.from(endTemporalAccessor);
		    ZonedDateTime startZonedDateTime = ZonedDateTime.of(startLocalDateTime, ZoneId.systemDefault());
		    ZonedDateTime endZonedDateTime = ZonedDateTime.of(endLocalDateTime, ZoneId.systemDefault());
		    Instant startInstant = Instant.from(startZonedDateTime);
		    Instant endInstant = Instant.from(endZonedDateTime);
			logger.log(Level.INFO, "startInstant: "+startInstant.toString()+"; endInstant: "+endInstant.toString());
			long startTime = startInstant.toEpochMilli();
			long endTime = endInstant.toEpochMilli();
			logger.log(Level.INFO, "startTime: "+startTime+", endTime: "+endTime);
			MongoClient mongoClient = MongoClients.create();
			MongoDatabase database = mongoClient.getDatabase("restdata");
			MongoCollection<Document> collection = database.getCollection("source1");
			logger.log(Level.INFO, "collection.countDocuments() "+collection.countDocuments());
			/*Document query = new Document("timestamp", new Document("$gte", zdtStart.toEpochSecond())
			          .append("$lte", zdtEnd.toEpochSecond()));
			//FindIterable<Document> result = collection.find(query);*/
			FindIterable<Document> result = collection.find().limit(1);
			result = collection.find(and(gte("timestamp", startTime), lte("timestamp", endTime)));
			results.put("source", result.first());
			// test data
			JSONObject value = new JSONObject();
			JSONArray updates = new JSONArray();
			for (int i = 0; i < 1000; i++) {
				value.put("name", "Bob").put("git", "hello world!").put("timestamp", endTime);
				updates.put(value);
			}
			value = new JSONObject();
			value.put("source", "gitters").put("updates", updates).put("timestamp", endTime);
			results = value;
			/*Document document = new Document("name", "Cafe Con Leche")
		               .append("contact", new Document("phone", "228-555-0149")
		                                       .append("email", "cafeconleche@example.com")
		                                       .append("location",Arrays.asList(-73.92502, 40.8279556)))
		               .append("stars", 3)
		               .append("categories", Arrays.asList("Bakery", "Coffee", "Pastries"));
			logger.log(Level.INFO, "document.toString() "+document.toString());
			results = new JSONObject(document.toJson());
			logger.log(Level.INFO, "results.toString() "+results.toString());*/
			logger.log(Level.INFO, "results.toString() "+results.toString());
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.toString());
		}
		return results.toString();
	}
}
