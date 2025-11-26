package edu.uclm.alarcos.qmutator.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.alarcos.qmutator.annealing.Combination;
import edu.uclm.alarcos.qmutator.annealing.g.GH;
import edu.uclm.alarcos.qmutator.annealing.g.test.ProgressiveTester;

@RestController
@RequestMapping("anngtest")
public class AnnealingControllerGTester {
	
	@PostMapping("/getCombinations")
	public HashMap<String, Object> getCombinations(HttpSession session, @RequestBody Map<String, Object> info) {
		JSONObject jso = new JSONObject(info);
		GH h = new GH();
		
		JSONObject jsoParProblem = jso.getJSONObject("gparProblem");
		JSONArray jsaRules = jsoParProblem.getJSONArray("rules");
		
		ProgressiveTester pt = new ProgressiveTester(h);
		pt.setParameters(jso.getJSONArray("parameters"));
		pt.setXCardinals(jso.getString("xCardinals"));
		pt.setVariables(jso.getJSONArray("variables"));
		
		for (int i=0; i<jsaRules.length(); i++) {
			JSONObject jsoRule = jsaRules.getJSONObject(i);
			h.newRule(jsoRule, true);
		}
		
		String algorithm = jso.getString("algorithm");
		session.setAttribute("pt", pt);
		session.setAttribute("algorithm", algorithm);
		
		return pt.getCombinations(algorithm);
	}
	
	@PostMapping("/executeTest")
	public HashMap<String, Object> executeTest(HttpSession session, @RequestBody Map<String, Object> info) {
		try {
			JSONObject jso = new JSONObject(info);
			
			JSONArray jsaParameterNames = jso.getJSONArray("parameterNames");
			JSONArray jsaValues = jso.getJSONArray("values");
			
			ProgressiveTester pt = (ProgressiveTester) session.getAttribute("pt");
			List<Combination> combinations = pt.executeTest(jsaParameterNames, jsaValues);
			
			HashMap<String, Object> result = new HashMap<>();
			result.put("combinations", combinations);
			result.put("parameterNames", info.get("parameterNames"));
			result.put("values", info.get("values"));
			return result;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}	
	
	@GetMapping("/executeAll")
	public void executeAll(HttpSession session) {
		ProgressiveTester pt = (ProgressiveTester) session.getAttribute("pt");
		String algorithm = session.getAttribute("algorithm").toString();
		pt.executeAll(algorithm);
	}
}
