package app;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/**
 * 
 * @author Arosha
 * @date 20-01-2019 Application starter
 */
public class AppMain {

	private static String inputFile = "./src/resources/input.csv";//input csv file path and file name
	
	private static String outputFile = "./src/resources/output.csv";//output csv file path and file name
	
	
	public static void main(String[] args) {

		BufferedReader br = null;
		Writer writer = null;
		String line = "";
		String cvsSplitBy = ",";// input csv - values separated by commas

		try {
			
			System.out.println("Reading input file..");

			br = new BufferedReader(new FileReader(inputFile));
			
			writer = new FileWriter(outputFile);

			while ((line = br.readLine()) != null) {

				try {
					String[] inputvals = line.split(cvsSplitBy);//read values seperated by commas

					String dateTime = inputvals[0];
					String geoCodes = inputvals[1] + "," + inputvals[2];

					//pass datetime and geocodes to get the localized datetime and timezone
					TimeZoneApp timeZoneApp = new TimeZoneApp(dateTime, geoCodes);
					String localTimeZone = timeZoneApp.getLocalizedDateTimeWithZone();

					//write the values into the output csv file
					new FileWriterApp().writeLine(writer,Arrays.asList(dateTime,inputvals[1],inputvals[2],localTimeZone));
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			System.out.println("End of writing output file..");

		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				writer.flush();
				writer.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			
		}

	}

}
