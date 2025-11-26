package edu.uclm.alarcos.qmutator.qiskit;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.uclm.alarcos.qmutator.http.HttpClient;

public class QiskitAnalyzer implements IFitnesser {

	private JSONObject originalResult;
	private JSONObject mutantResult;

	public void setOriginalResult(JSONObject originalResult) {
		this.originalResult = originalResult;
	}

	public void setMutantResult(JSONObject mutantResult) {
		this.mutantResult = mutantResult;
	}

	public String calculate() {
		JSONObject payload = new JSONObject();
		payload.put("mode", "signedrank");
		payload.put("options", new JSONObject());
		payload.put("labels", new JSONArray().put("Original").put("Mutant"));
		
		JSONArray sets = new JSONArray();
		JSONArray set1 = new JSONArray();
		JSONArray outputLines = this.originalResult.getJSONArray("outputLines");
		int frequency, order;
		JSONObject jso;
		for (int i=0; i<outputLines.length(); i++) {
			jso = outputLines.getJSONObject(i);
			frequency = jso.getInt("frequency");
			order = jso.getInt("order");
			for (int j=0; j<frequency; j++)
				set1.put(order);
		}
		
		JSONArray set2 = new JSONArray();
		outputLines = this.mutantResult.getJSONArray("outputLines");
		for (int i=0; i<outputLines.length(); i++) {
			jso = outputLines.getJSONObject(i);
			frequency = jso.getInt("frequency");
			order = jso.getInt("order");
			for (int j=0; j<frequency; j++)
				set2.put(order);
		}
		
		sets.put(set1);
		sets.put(set2);
		
		payload.put("sets", sets);
				
		JSONArray headers = new JSONArray().
				put("content-type:application/json").
				put("Authority:www.aatbio.com").
				put("Scheme:https").
				put("Referer:https://www.aatbio.com/tools/mann-whitney-wilcoxon-signed-rank-test-calculator").
				put("User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
		
		HttpClient client = new HttpClient();
		String result = client.sendPost("https://www.aatbio.com/api/tools/graph", headers, payload);
		return result;
	}

	
}
