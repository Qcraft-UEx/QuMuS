package edu.uclm.alarcos.qmutator.annealing.g;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.alarcos.qmutator.annealing.CH;

public class GH {
	
	private ArrayList<GRule> rules = new ArrayList<>();
	
	private HashMap<String, GParameter> parameters = new HashMap<>();
	private HashMap<String, GSet> sets = new HashMap<>();
	private HashMap<String, GVariable> variables = new HashMap<>();
	
	private CH function;
	
	public GRule newRule() {
		GRule rule = new GRule(this);
		this.rules.add(rule);
		return rule;
	}
	
	public GRule newRule(JSONObject jsoRule, boolean withDetails) {
		GRule rule = new GRule(this, jsoRule, withDetails);
		this.rules.add(rule);
		return rule;
	}

	public void addParameter(GParameter parameter) {
		this.parameters.put(parameter.getParameter(), parameter);
	}

	public GParameter findParameter(String name) {
		return this.parameters.get(name);
	}

	public CH instantiate() {
		this.function.instantiateVariables(this.variables);
		
		this.function.getExpressions().clear();
		for (int i=0; i<this.rules.size(); i++) {
			GRule rule = this.rules.get(i);
			rule.fillFunction(this.function);
		}
		this.function.prepare();
		return this.function;
	}
	
	public CH setXCardinals(Integer... cardinals) {
		this.function = new CH();
		this.function.setX("x", cardinals);
		return this.function;
	}
	
	public CH setXCardinals(ArrayList<Integer> cardinals) {
		this.function = new CH();
		this.function.setX("x", cardinals);
		return this.function;
	}

	@JsonIgnore
	public int getXCardinal() {
		return this.function.getXCardinal();
	}
	
	private JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		JSONArray jsa = new JSONArray();
		for (GRule rule : this.rules)
			jsa.put(rule.toJSON()); 
		jso.put("h", jsa);
		return jso;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (GRule rule : this.rules)
			sb.append(rule.toString() + "\n");
		
		Collection<GParameter> eParameters = this.parameters.values();
		for (GParameter parameter : eParameters)
			sb.append(parameter.toString() + "\n");
		
		Collection<GVariable> eVariables = this.variables.values();
		for (GVariable variable : eVariables)
			sb.append(variable.toString() + "\n");
		
		if (this.function!=null)
			sb.append(this.function.getX().toString());
		return sb.toString();
	}

	public void setParameterValue(String parameter, double value) {
		GParameter par = this.findParameter(parameter);
		if (par!=null)
			par.setValue(value);
	}
	
	public void setSetValues(String setName, Integer... values) {
		GSet set = this.findSet(setName);
		if (set!=null)
			set.setValues(values);
	}

	public GParameter newParameter(String name) {
		GParameter par = this.findParameter(name);
		if (par!=null)
			return par;
		par = new GParameter(this, name, null);
		this.addParameter(par);
		return par;
	}
	
	public GSet newSet(String name) {
		GSet set = this.findSet(name);
		if (set!=null)
			return set;
		set = new GSet(this, name);
		this.addSet(set);
		return set;
	}
	
	private void addSet(GSet set) {
		this.sets.put(set.getName(), set);
	}

	private GSet findSet(String name) {
		return this.sets.get(name);
	}

	public void setVariable(String variableName, Double value, int... coords) {
		GVariable v = this.findVariable(variableName);
		v.setValue(value, coords);
	}

	public GVariable findVariable(String variableName) {
		return this.variables.get(variableName);
	}
	
	public GVariable putVariable(String variableName) {
		GVariable v = new GVariable(variableName);
		this.variables.put(variableName, v);
		return v;
	}

	public void addVariable(GVariable variable) {
		this.variables.put(variable.getName(), variable);
	}

	public GVariable newVariable(String name, int... dimensions) {
		GVariable variable = new GVariable(name);
		variable.setDimensions(dimensions);
		this.variables.put(name, variable);
		return variable;
	}
	
	public GVariable newVariable(String name, ArrayList<Integer> dimensions) {
		GVariable variable = new GVariable(name);
		variable.setDimensions(dimensions);
		this.variables.put(name, variable);
		return variable;
	}

	public void setXValue(int value, int... coords) {
		this.function.setXValue(value, coords);
	}
	
	@JsonIgnore
	public CH getFunction() {
		return function;
	}

	@JsonIgnore
	public ArrayList<GRule> getRules() {
		return rules;
	}

	@JsonIgnore
	public HashMap<String, GParameter> getParameters() {
		return parameters;
	}

	@JsonIgnore
	public HashMap<String, GVariable> getVariables() {
		return variables;
	}
	
	public JSONObject getThis() {
		return this.toJSON();
	}
}
