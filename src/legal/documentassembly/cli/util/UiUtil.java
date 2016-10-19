package legal.documentassembly.cli.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UiUtil {

	private static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

	public static String readValue(String prompt) {
		String retVal = "";
		System.out.print(prompt + ": ");
		try {
			retVal = bufferedReader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}
	
	public static int readInt(String prompt, int defaultValue) {
		String value = readValue(prompt);
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return defaultValue;
	}
	
}