package org.vpac.grisu.clients.gridTests;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class Utils {

	public static ArrayList<String> fromException(Throwable e) {

		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		e.printStackTrace(pw);
		pw.flush();
		sw.flush();
		final ArrayList<String> result = new ArrayList<String>();
		final String[] stacktrace = sw.toString().split("\n");
		for (final String st : stacktrace) {
			result.add(st + "\n");
		}
		return result;
	}

	public static String stringFromException(Throwable e) {

		final StringBuffer result = new StringBuffer();
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		e.printStackTrace(pw);
		pw.flush();
		sw.flush();
		final String[] stacktrace = sw.toString().split("\n");
		for (final String st : stacktrace) {
			result.append(st + "\n");
		}
		return result.toString();
	}

}
