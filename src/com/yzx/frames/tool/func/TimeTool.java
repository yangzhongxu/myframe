package com.yzx.frames.tool.func;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeTool {

	public static String format(String inputValue, String inputFormat, String outputFormat) {
		long million = stringToMillion(inputValue, inputFormat);
		return millionToString(million, outputFormat);
	}

	// million -> ×Ö·û´®
	public static String millionToString(long million, String outputFormat) {
		SimpleDateFormat sFormat = new SimpleDateFormat(outputFormat, Locale.getDefault());
		return sFormat.format(new Date(million));
	}

	// ×Ö·û´® -> million
	public static long stringToMillion(String inputStr, String inputformat) {
		SimpleDateFormat f = new SimpleDateFormat(inputformat, Locale.getDefault());
		try {
			return f.parse(inputStr).getTime();
		} catch (Exception e) {
			return 0;
		}
	}

}
