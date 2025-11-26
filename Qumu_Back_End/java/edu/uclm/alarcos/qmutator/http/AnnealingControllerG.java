package edu.uclm.alarcos.qmutator.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.g.GH;
import edu.uclm.alarcos.qmutator.annealing.g.GIndexedSummation;
import edu.uclm.alarcos.qmutator.annealing.g.GIntValue;
import edu.uclm.alarcos.qmutator.annealing.g.GParProblem;
import edu.uclm.alarcos.qmutator.annealing.g.GParameter;
import edu.uclm.alarcos.qmutator.annealing.g.GProduct;
import edu.uclm.alarcos.qmutator.annealing.g.GRule;
import edu.uclm.alarcos.qmutator.annealing.g.GTextRule;
import edu.uclm.alarcos.qmutator.annealing.g.GUseOfX;
import edu.uclm.alarcos.qmutator.annealing.g.GVariable;
import edu.uclm.alarcos.qmutator.dao.GParProblemRepository;
import edu.uclm.alarcos.qmutator.model.Manager;

@RestController
@RequestMapping("annpar")
public class AnnealingControllerG {
	@Autowired
	private GParProblemRepository ppdao;
	
	@GetMapping("/getProblems")
	public List<IProblemDescription> getProblems() {
		return Manager.get().getGparProblemRepo().getNames();
	}
	
	@GetMapping("/getProblem")
	public GParProblem getProblem(@RequestParam String problemId) {
		GParProblem problem = Manager.get().getGparProblemRepo().findById(problemId).get();
		/*List<GTextRule> rules = problem.getRules();
		for (GTextRule rule : rules) {
			if (rule.getMathML()==null) {
				Qumu2Math parser = new Qumu2Math(new JSONObject(rule.getText()));
				String s = parser.buildRule();
				rule.setMathML(s);
				System.out.println(s);
			}
		}
		ppdao.save(problem);*/
		return problem;
	}
	
	@PostMapping("/save")
	public void save(@RequestBody Map<String, Object> info) {
		JSONObject jso = new JSONObject(info);
		GParProblem gparProblem = new GParProblem();
		gparProblem.setName(jso.optString("name"));
		gparProblem.setVariables(jso.optString("variables"));
		gparProblem.setParameters(jso.optString("parameters"));
		JSONArray jsaRules = jso.getJSONArray("rules");
		for (int i=0; i<jsaRules.length(); i++) {
			JSONObject jsoRule = jsaRules.getJSONObject(i);
			GTextRule gtr = new GTextRule();
			gtr.setGparProblem(gparProblem);
			if (jsoRule.has("lambda"))
				gtr.setLambda(jsoRule.getDouble("lambda"));
			else
				gtr.setLambda(1.0);
			gtr.setText(jsoRule.getJSONObject("text").toString());
			gtr.setMathML(jsoRule.getString("mathML"));
			gparProblem.addRule(gtr);
		}
		Manager.get().getGparProblemRepo().save(gparProblem);
	}
	
	@PostMapping("/compose")
	public  Map<String, Object> compose(HttpServletResponse response, @RequestBody Map<String, Object> info) {
		JSONObject jso = new JSONObject(info);
		
		GH h = new GH();
			
		JSONObject jsoParProblem = jso.getJSONObject("gparProblem");
		JSONArray jsaRules = jsoParProblem.getJSONArray("rules");
		
		for (int i=0; i<jsaRules.length(); i++) {
			JSONObject jsoRule = jsaRules.getJSONObject(i);
			h.newRule(jsoRule, true);
		}
		
		loadVariables(h, jso.getJSONArray("variables"));
		loadParameters(h, jso.getJSONArray("parameters"));
		
		ArrayList<Integer> xCardinals = this.getIntValues(jso.getString("xCardinals"));
		
		h.setXCardinals(xCardinals);
		
		CH cf = h.instantiate();
		JSONObject jsoResult = cf.toJSON();
		Map<String, Object> result = jsoResult.toMap();
		return result;
	}
	
	@PostMapping("/translateToQuMu")
	public Map<String, Object> translateToQuMu(@RequestBody Map<String, Object> info) {
		try {
			JSONObject jso = new JSONObject(info);
			String fromLeft = jso.optString("textGExprFromLeft");
			String fromRight = jso.optString("textGExprFromRight");
			String to = jso.optString("textGExprTo");
			String left = jso.optString("textGExprLeft");
			String right = jso.optString("textGExprRight");
			JSONArray forAlls = jso.optJSONArray("textGExprForAlls");
			
			GH h = new GH();
			
			GRule rule = h.newRule();
			GIndexedSummation summation = rule.newIndexedSummation();
			summation.newFrom(fromLeft, Integer.parseInt(fromRight));
			GParameter gTo = new GParameter(h);
			gTo.setParameter(to);
			summation.setTo(gTo);
			
			if (left.indexOf('*')!=-1 || left.indexOf('·')!=-1) {
				GProduct product = new GProduct(left);
				summation.setBody(product);
			} else {
				int posBarra = left.indexOf('_');
				left = left.substring(posBarra+1);
				String[] tokens = left.split("_");
				GUseOfX guox = new GUseOfX(tokens);
				summation.setBody(guox);
			}
			if (right.length()>0) {
				try {
					int value = Integer.parseInt(right);
					GIntValue gRight = new GIntValue(value);
					rule.setRight(gRight);
				} catch (Exception e) {
					GParameter par = new GParameter(h);
					par.setParameter(right);
					rule.setRight(par);
				}
			}
			
			for (int i=0; i<forAlls.length(); i++) {
				JSONObject jsoForAll = forAlls.getJSONObject(i);
				String variable = jsoForAll.optString("variable");
				if (variable.length()==0)
					continue;
				int from = jsoForAll.getInt("start");
				GParameter par = h.newParameter(jsoForAll.getString("end"));
				rule.newIntervalForAll(variable, new GIntValue(from), par);
			}
			
			return rule.toJSON().toMap();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	private void loadParameters(GH h, JSONArray jsa) {
		for (int i=0; i<jsa.length(); i++) {
			JSONObject jsoPar = jsa.getJSONObject(i);
			String parName = jsoPar.getString("name");
			GParameter par = h.findParameter(parName);
			if (par!=null) {
				Double value = jsoPar.getDouble("value");
				h.setParameterValue(parName, value);
			}
		}
	}

	private void loadVariables(GH h, JSONArray jsa) {
		for (int i=0; i<jsa.length(); i++) {
			JSONObject jsoVar = jsa.getJSONObject(i);
			GVariable variable = h.findVariable(jsoVar.getString("name"));
			if (variable!=null) {
				variable.setDimensions(this.getIntValues(jsoVar.getString("cardinals")));
				ArrayList<Double> values = getDoubleValues(jsoVar.getString("values"));
				variable.setValues(values);
			}
		}
	}

	private ArrayList<Double> getDoubleValues(String values) {
		ArrayList<Double> result = new ArrayList<>();
		String[] tokens = values.split(",");
		for (String token : tokens)
			result.add(Double.parseDouble(token.trim()));
		return result;
	}
	
	private ArrayList<Integer> getIntValues(String values) {
		ArrayList<Integer> result = new ArrayList<>();
		String[] tokens = values.split(",");
		for (String token : tokens)
			result.add(Integer.parseInt(token.trim()));
		return result;
	}
}
