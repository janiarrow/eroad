package app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * 
 * @author Arosha
 * @date 20-01-2019
 */
public class TimeZoneApp {

	private String apiKey = "AIzaSyByhRf-kjGuGnpXF-0kV68IWQTqn3Jp7e8";// API key generated from google API services

	private String dateTime = null;
	private String geoCodes = null;
	private long timeStmp;

	public TimeZoneApp() {
		System.out.println("Default constructor called. No geo location or date set.");
	}

	public TimeZoneApp(String dateTime, String geoCodes) {
		this.dateTime = dateTime;
		this.geoCodes = geoCodes;
	}

	/**
	 * 
	 * @return timezone and localized datetime
	 */
	public String getLocalizedDateTimeWithZone() throws Exception{

		if(null==dateTime) {
			throw new Exception("No date time is set");
		}
		
		java.sql.Timestamp ts = java.sql.Timestamp.valueOf(dateTime);

		timeStmp = ts.getTime() / 1000;

		// Call the google API service to get the data response
		String responseStr = getResponse();

		// read the json response
		String localDateTimeZone = readJson(responseStr);

		return localDateTimeZone;
	}

	/**
	 * Calls google Time Zone API services to get the time zones for the locations of the geocodes
	 *  and time offset from UTC for the locations
	 *  
	 * @return the json response which was returned from the time zone service 
	 */
	private String getResponse() throws Exception{

		if(null==geoCodes) {
			throw new Exception("No longitude and latitude provided");
		}
		
		String URLStr = "https://maps.googleapis.com/maps/api/timezone/json?location=" + geoCodes + "&timestamp="
				+ timeStmp + "&key=" + apiKey;

		StringBuilder output = null;

		try {

			URL url = new URL(URLStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			output = new StringBuilder();
			String line;

			while ((line = br.readLine()) != null) {
				output.append(line);
			}

			conn.disconnect();

		} catch (Exception e) {

			e.printStackTrace();

		}

		return output.toString();

	}

	/**
	 * 
	 * @param takes in json response string
	 * @return 
	 */
	private String readJson(String response) {

		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(response);

			JSONObject jsonObject = (JSONObject) obj;
			
			
			String status = (String) jsonObject.get("status");
			
			if("OK".equals(status)) {
				
				long dstOffset =  (Long)jsonObject.get("dstOffset");
				long rawOffset =  (Long)jsonObject.get("rawOffset");

				String timeZoneID = (String) jsonObject.get("timeZoneId");
				
				return timeZoneID+","+getLocalizedDateTime(dstOffset, rawOffset);
				
			}else {
				String errorMessage = (String) jsonObject.get("timeZoneId");
				
				if(null!=errorMessage) {
					throw new Exception(status+" - "+errorMessage);
				}else {
					throw new Exception(status);
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	/**
	 * 
	 * @param dstOffSet parsed from json response
	 * @param rawOffSet parsed from json response
	 * @return the localized datetime in 24hr format
	 * 
	 * Calculate the localized date time with the sum of offset values and timestamp 
	 */
	private String getLocalizedDateTime(long dstOffSet, long rawOffSet) {
		
		long sumInSec = (timeStmp + dstOffSet + rawOffSet);
		
		Date d = new Date(sumInSec *1000);
		DateFormat dateFormatt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		return dateFormatt.format(d);
	}
}
