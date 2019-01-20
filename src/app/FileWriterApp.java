package app;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * 
 * @author Arosha
 * Write the output to file
 * 
 */
public class FileWriterApp {

	private static final char DEFAULT_SEPARATOR = ',';

	boolean first = true;

	/**
	 * 
	 * @param value 
	 * @return validated value according to csv format
	 */
	private String followCVSformat(String value) {

		String result = value;
		if (result.contains("\"")) {
			result = result.replace("\"", "\"\"");
		}
		return result;

	}

	/**
	 * 
	 * @param w - writer object, csv file passed from appMain
	 * @param values - input file values and new values as a List of Strings
	 * @throws IOException 
	 * 
	 * Write the output file line by line with the values seperated with commas
	 */
	public  void writeLine(Writer w, List<String> values) throws IOException {

		StringBuilder sb = new StringBuilder();

		for (String value : values) {

			if (!first) {
				sb.append(DEFAULT_SEPARATOR);
			}
			
			sb.append(followCVSformat(value));

			first = false;
		}

		sb.append("\n");
//		System.out.println(sb.toString());
		w.append(sb.toString());
	}

}
