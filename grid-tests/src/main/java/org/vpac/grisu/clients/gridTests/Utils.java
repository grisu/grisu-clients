package org.vpac.grisu.clients.gridTests;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class Utils {

	public static ArrayList<String> fromException(Throwable e) {

		StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        pw.flush();
        sw.flush();
        ArrayList<String> result = new ArrayList<String>();
        String[] stacktrace = sw.toString().split("\n");
        for ( String st : stacktrace ) {
        	result.add(st+"\n");
        }
        return result;
	}
	
	public static String stringFromException(Throwable e) {

		StringBuffer result = new StringBuffer();
		StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        pw.flush();
        sw.flush();
        String[] stacktrace = sw.toString().split("\n");
        for ( String st : stacktrace ) {
        	result.append(st+"\n");
        }
        return result.toString();
	}
	
}
