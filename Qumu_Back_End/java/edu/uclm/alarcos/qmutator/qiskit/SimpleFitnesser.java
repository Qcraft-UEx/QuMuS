package edu.uclm.alarcos.qmutator.qiskit;

import org.json.JSONArray;
import org.json.JSONObject;

public class SimpleFitnesser {
	
	private SimpleFitnesser() {}

	public static double calculate(JSONObject originalResult, JSONObject mutantResult) {
		String input = mutantResult.getString("input");
		JSONArray mutantLines = mutantResult.getJSONArray("outputLines");
		
		JSONArray originalResults = originalResult.getJSONArray("results");
		JSONObject jso = null;
		JSONArray originalLines = new JSONArray();
		for (int i=0; i<originalResults.length(); i++) {
			jso = originalResults.getJSONObject(i);
			if (jso.getString("input").equals(input)) {
				originalLines = jso.getJSONArray("outputLines");
				break;
			}				
		}
		
		int shots = 0;
		int totalError = 0;
		for (int i=0; i<originalLines.length(); i++) {
			jso = originalLines.getJSONObject(i);
			int originalFrequency = jso.getInt("frequency");
			int originalOrder = jso.getInt("order");
			shots = shots + originalFrequency;
			totalError = totalError + error(mutantLines, originalOrder, originalFrequency);
		}
		return (1.0*totalError)/(2*shots);
	}

	private static int error(JSONArray mutantLines, int originalOrder, int originalFrequency) {
		int error = 0;
		boolean found = false;
		for (int i=0; i<mutantLines.length(); i++) {
			JSONObject jso = mutantLines.getJSONObject(i);
			if (jso.getInt("order")==originalOrder) { 
				error = originalFrequency-jso.getInt("frequency");
				found = true;
				break;
			}
		}
		if (!found)
			error = originalFrequency;
		if (error<0)
			error = -error;
		return error;
	}

	
}
