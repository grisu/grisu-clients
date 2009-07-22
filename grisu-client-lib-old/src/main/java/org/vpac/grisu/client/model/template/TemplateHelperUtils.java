package org.vpac.grisu.client.model.template;

import java.util.Map;
import java.util.TreeMap;

public class TemplateHelperUtils {
	
	public static final String DELIMITER = ",";
	public static final String KEY_VALUE_DELIMITER = "=";
	
	public static final String NO_VALUE = "true";
	
	public static String getParameterName(String parameterNameWithOptionalConfig) {
		
		if (  checkValidParameterValue(parameterNameWithOptionalConfig) ) {
			
			int start = parameterNameWithOptionalConfig.indexOf("[");
			if ( start == -1 ) {
				return parameterNameWithOptionalConfig;
			} else {
				String output = parameterNameWithOptionalConfig.substring(0, start);
				return output;
			}
			
		} else {
			throw new RuntimeException("Invalid configuration in template: "+parameterNameWithOptionalConfig);
		}
		
	}
	
	public static Map<String, String> getConfig(String parameterNameWithOptionalConfig) {
		
		
		if ( checkValidParameterValue(parameterNameWithOptionalConfig) ) {

			Map<String, String> result = new TreeMap<String, String> ();
			int start = parameterNameWithOptionalConfig.indexOf("[");

			if ( start == -1 ) {
				return result;
			}
			
			int end = parameterNameWithOptionalConfig.indexOf("]");
			
			String config = parameterNameWithOptionalConfig.substring(start+1, end);
			
			for ( String configOption : config.split(DELIMITER) ) {
				
				String key = null;
				String value = null;
				
				if ( configOption.contains(KEY_VALUE_DELIMITER) ) {
					
					int indexDelimiter = configOption.indexOf(KEY_VALUE_DELIMITER);
					
					key = configOption.substring(0, indexDelimiter);
					value = configOption.substring(indexDelimiter+1);
					
				} else {
					key = configOption;
					value = NO_VALUE;
				}
				result.put(key, value);
			}
			
			return result;
			
		} else {
			throw new RuntimeException("Invalid configuration in template: "+parameterNameWithOptionalConfig);
		}
		
	}
	
	public static boolean checkValidParameterValue(String parameterNameWithOptionalConfig) {
		
		if ( parameterNameWithOptionalConfig.contains("[") ) {
			int start = parameterNameWithOptionalConfig.indexOf("[");
			int end = parameterNameWithOptionalConfig.indexOf("]");
			
			if ( start > end || end > parameterNameWithOptionalConfig.length() ) {
				return false;
			}
			
			return true;
			
		} else {
			return true;
		}
		
		
	}

}
