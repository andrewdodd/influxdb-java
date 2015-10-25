package org.influxdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.joda.time.DateTime;

public class Diagnostics {
	private Map<String, Object> system = new HashMap<>();
//	private Map<String, Object> build = new HashMap<>();
//	private Map<String, Object> runtime = new HashMap<>();
	
	private Diagnostics() {
		
	}
	
	public static Diagnostics fromQueryResult(QueryResult queryResult) {
		Diagnostics diagnostics = new Diagnostics();
		for (Result result: queryResult.getResults()) {
			for (Series series: result.getSeries()){
				switch (series.getName()) {
				case "system":
					diagnostics.system = flattenFromValues(series.getColumns(), series.getValues().get(0));
					break;
//				case "build":
//					diagnostics.build = flattenFromValues(series.getColumns(), series.getValues().get(0));
//					break;
//				case "runtime":
//					diagnostics.runtime = flattenFromValues(series.getColumns(), series.getValues().get(0));
//					break;
				default:
					break;
				}
			}
		}
		
		return diagnostics;
	}
	private static Map<String, Object> flattenFromValues(List<String> columns, List<Object> values) {
		Map<String, Object> flattened = new HashMap<>();
		
		for (int i = 0; i < columns.size(); i++) {
			String column = columns.get(i);
			flattened.put(column, values.get(i));
		}
		
		return flattened;
	}
	
	public DateTime getCurrentTime() {
		return parseTime(system.get("currentTime"));
	}
	
	public DateTime getStartedTime() {
		return parseTime(system.get("started"));
	}

	private static DateTime parseTime(Object object) {
		if (object == null) {
			return null;
		}
		
		if (object instanceof String) {
			
			String currentTimeString = (String) object;
			return DateTime.parse(currentTimeString);
		}
		
		return null;

	}
}