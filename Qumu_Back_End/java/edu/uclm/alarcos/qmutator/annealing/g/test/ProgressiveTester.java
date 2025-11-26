package edu.uclm.alarcos.qmutator.annealing.g.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.Combination;
import edu.uclm.alarcos.qmutator.annealing.g.GH;
import edu.uclm.alarcos.qmutator.annealing.g.GParameter;
import edu.uclm.alarcos.qmutator.annealing.g.GVariable;

public class ProgressiveTester {
	private GH h;
	private ArrayList<TestParameter> parameters;
	private ArrayList<TestParameter> xCardinals;
	private ArrayList<TestVariable> variables;
	private HashMap<String, TestVariable> variablesMap;

	public ProgressiveTester(GH h) {
		this.h = h;
		this.parameters = new ArrayList<>();
		this.xCardinals = new ArrayList<>();
		this.variablesMap = new HashMap<>();
		this.variables = new ArrayList<>();
	}

	public void setParameters(JSONArray jsaParameters) {
		JSONObject jsoPar;
		TestParameter par;
		for (int i=0; i<jsaParameters.length(); i++) {
			jsoPar = jsaParameters.getJSONObject(i);
			par = new TestParameter(jsoPar.getString("name"));
			par.setValues(jsoPar.getString("value"));
			this.parameters.add(par);
		}
	}

	public void setXCardinals(String xCardinals) {
		String[] st = xCardinals.split(",");
		String parameterName;
		TestParameter testParameter;
		for (int i=0; i<st.length; i++) {
			parameterName = st[i].trim();
			testParameter = this.findParameter(parameterName);
			if (testParameter==null) 
				continue;
			this.xCardinals.add(testParameter);
		}
	}

	public void setVariables(JSONArray jsaVariables) {
		for (int i=0; i<jsaVariables.length(); i++) {
			JSONObject jsoVar = jsaVariables.getJSONObject(i);
			String varName = jsoVar.getString("name");
			TestVariable variable = new TestVariable(varName);
			String cardinals = jsoVar.getString("cardinals");
			variable.setCardinals(this.parameters, cardinals);
			variable.setTestValues(jsoVar.getString("values"));
			this.variablesMap.put(varName, variable);
			this.variables.add(variable);
		}
	}

	public HashMap<String, Object> getCombinations(String algorithm) {
		GParameter gp;
		for (TestParameter parameter : this.parameters) {
			gp = h.findParameter(parameter.getName());
			gp.setValue(parameter.getValue());
		}
		ArrayList<Integer> cardinals = new ArrayList<>();
		for (TestParameter xCardinal : this.xCardinals) 
			cardinals.add((int) xCardinal.getValue());
		h.setXCardinals(cardinals);
		
		GVariable variable;
		for (TestVariable testVariable : this.variables) {
			variable = h.findVariable(testVariable.getName());
			variable.setDimensions(testVariable.getInstantiatedCardinals());
		}
		
		Algorithm a;
		switch (algorithm) {
		case "All" : 
			a = new AllCombinationsAlgorithm();
			break;
		case "EachChoice" :
			a = new EachChoiceAlgorithm();
			break;
		case "Pairwise" :
			a = new AETGAlgorithm();
			break;
		default:
			a = new AllCombinationsAlgorithm();
		}
		a.setParameters(this.parameters);
		List<TestCombination> ttcc = a.buildCombinations();
		HashMap<String, Object> result = new HashMap<>();
		
		ArrayList<String> parameterNames = new ArrayList<>();
		for (TestParameter p : this.parameters)
			parameterNames.add(p.getName());
		result.put("parameterNames", parameterNames);
		result.put("combinations", ttcc);
		return result;
	}
	
	public GH getH() {
		return h;
	}

	@SuppressWarnings("unchecked")
	public void executeAll(String algorithm) {
		HashMap<String, Object> combinations = this.getCombinations(algorithm);
		ArrayList<String> parameterNames = (ArrayList<String>) combinations.get("parameterNames");
		ArrayList<TestCombination> testCombinations = (ArrayList<TestCombination>) combinations.get("combinations");
		
		JSONArray jsaParameterNames = new JSONArray();
		for (String parameterName : parameterNames)
			jsaParameterNames.put(parameterName);
		
		for (TestCombination tc : testCombinations) {
			JSONArray jsaValues = new JSONArray();
			for (int i=0; i<tc.size(); i++)
				jsaValues.put(tc.getValueIndex(i));
			executeTest(jsaParameterNames, jsaValues);
		}
	}

	public List<Combination> executeTest(JSONArray jsaParameterNames, JSONArray jsaValues) {
		String parameterName;
		TestParameter xCardinal;
		ArrayList<Integer> cardinals = new ArrayList<>();
		double currentValue; 
		
		for (int i=0; i<jsaParameterNames.length(); i++) {
			parameterName = jsaParameterNames.getString(i);
			currentValue = jsaValues.getDouble(i);
			this.findParameter(parameterName).setCurrentValue(currentValue);
			this.h.setParameterValue(parameterName, currentValue);
		}
		
		for (int i=0; i<this.xCardinals.size(); i++) {
			xCardinal = this.xCardinals.get(i);
			currentValue = jsaValues.getDouble(i);
			for (int j=0; j<jsaParameterNames.length(); j++) {
				parameterName = jsaParameterNames.getString(j);
				if (xCardinal.getName().equals(parameterName)) {
					cardinals.add((int) currentValue);
				}
			}
		}
		h.setXCardinals(cardinals);
		
		TestVariable testVariable;
		TestParameter testParameter;
		GVariable variable;
		for (int i=0; i<this.variables.size(); i++) {
			testVariable = this.variables.get(i);
			cardinals.clear();
			for (int j=0; j<jsaParameterNames.length(); j++) {
				parameterName = jsaParameterNames.getString(j);
				currentValue = jsaValues.getDouble(j);
				for (int k=0; k<testVariable.getCardinals().size(); k++) {
					testParameter = testVariable.getCardinals().get(k);
					if (testParameter.getName().equals(parameterName))
						cardinals.add((int) currentValue);
				}
			}
			variable = h.newVariable(testVariable.getName(), cardinals);
			variable.setValues(testVariable.getTestValues());
		}
		
		CH ch = h.instantiate();
		
		return ch.calculateAll(null, null, false, false);
	}
	
	private TestParameter findParameter(String parameterName) {
		for (TestParameter testParameter : this.parameters)
			if (testParameter.getName().equals(parameterName))
				return testParameter;
		return null;
	}
}
