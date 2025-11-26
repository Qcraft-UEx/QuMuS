package edu.uclm.alarcos.qmutator.annealing.g;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.alarcos.qmutator.annealing.CH;

public class GRule {

	private Double lambda;
	private GH h;
	private GSummation left;
	private String sign;
	private GExpr right;
	private ArrayList<GForAll> forAlls;
	private HashMap<String, GIndex> indexes;
	
	public GRule(GH h) {
		this.h = h;
		this.forAlls = new ArrayList<>();
		this.indexes = new HashMap<>();
	}

	public GRule(GH h, JSONObject jsoRule, boolean withDetails) {
		this(h);
		
		if (withDetails)
			newRuleWithDetails(jsoRule);
		else
			newRuleWithNoDetails(jsoRule);
	}
	
	public String toML() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.left.toML());
		if (this.right!=null) {
			sb.append("<mo>" + this.sign + "</mo>");
			sb.append(this.right.toML());
		}
		if (!this.forAlls.isEmpty()) {
			sb.append("<mo>,</mo>");
			for (GForAll forAll : this.forAlls)
				sb.append(forAll.toML());
		}
		return sb.toString();
	}

	private void newRuleWithNoDetails(JSONObject jsoRule) {
		JSONObject jsoLeft = jsoRule.getJSONObject("left");
		this.left = new GIndexedSummation(this, jsoLeft, false, this.h);
		if (jsoRule.has("right")) {
			this.sign = jsoRule.optString("sign");
			if (this.sign.length()==0)
				this.sign = "=";
			JSONObject jsoRight = jsoRule.optJSONObject("right");
			if (jsoRight!=null) {
				this.right = GExpr.build(jsoRight, this.h);
			} else {
				this.right = new GIntValue(jsoRule.getInt("right"));
			}
		}
		this.buildForAlls(jsoRule);
	}

	private void newRuleWithDetails(JSONObject jsoRule) {
		if (jsoRule.has("lambda"))
			this.lambda = jsoRule.getDouble("lambda");
		else
			this.lambda = 1.0;
		
		JSONObject jsoExpr = jsoRule.getJSONObject("text");
		JSONObject jsoLeft = jsoExpr.optJSONObject("left");
		
		this.left = new GIndexedSummation(this, jsoLeft, true, this.h);
		
		if (jsoExpr.has("right")) {
			this.sign = jsoExpr.optString("sign");
			if (this.sign.length()==0)
				this.sign = "=";
			JSONObject jsoRight = jsoExpr.optJSONObject("right");
			if (jsoRight!=null) {
				this.right = GExpr.build(jsoRight, this.h);
			} else {
				this.right = new GIntValue(jsoExpr.getInt("right"));
			}
		}

		buildForAlls(jsoExpr);
	}

	private void buildForAlls(JSONObject jsoExpr) {
		this.forAlls = new ArrayList<>();
		JSONArray jsaForAlls = jsoExpr.optJSONArray("forAlls");
		if (jsaForAlls!=null) {
			for (int i=0; i<jsaForAlls.length(); i++) {
				JSONObject jsoForAll = jsaForAlls.getJSONObject(i);
				GForAll forAll;
				String type = jsoForAll.optString("type");
				if (type.length()==0 || type.equals(GIntervalForAll.class.getSimpleName()))
					forAll = new GIntervalForAll(jsoForAll, this.h);
				else
					forAll = new GSetForAll(jsoForAll);
				this.forAlls.add(forAll);
			}
		}
	}
	
	public GIndexedSummation newIndexedSummation() {
		this.left = new GIndexedSummation(this);
		return (GIndexedSummation) this.left;
	}
	
	public GSetSummation newSetSummation() {
		this.left = new GSetSummation(this);
		return (GSetSummation) this.left;
	}

	public void fillFunction(CH f) {
		this.left.getResultExpr(f, forAlls);
	}

	@Override
	public String toString() {
		String r = this.left.toString();
		if (this.right!=null)
			r = r + " " + this.sign + " " + this.right.toString();
		for (GForAll forAll : this.forAlls) {
			r = r + ", " + forAll.toString();
		}
		if (lambda!=null)
			r = r + "; lambda = " + this.lambda;
		return r;
	}

	public JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", this.getClass().getSimpleName());
		jso.put("left", this.left.toJSON());
		if (this.right!=null)
			jso.put("right", this.right.toJSON());
		jso.put("lambda", this.lambda);
		JSONArray jsa = new JSONArray();
		for (int i=0; i<this.forAlls.size(); i++)
			jsa.put(this.forAlls.get(i).toJSON());
		if (jsa.length()>0)
			jso.put("forAlls", jsa);
		return jso;
	}

	public Double getLambda() {
		return lambda;
	}

	public void setLambda(Double lambda) {
		this.lambda = lambda;
	}

	public GSummation getLeft() {
		return left;
	}

	public void setLeft(GIndexedSummation left) {
		this.left = left;
	}

	public GExpr getRight() {
		return right;
	}

	public void setRight(GExpr right) {
		this.right = right;
	}
	
	public void addForAll(GForAll forAll) {
		this.forAlls.add(forAll);
	}

	public GIndex findIndex(String indexName) {
		return this.indexes.get(indexName);
	}

	public void add(GIndex index) {
		this.indexes.put(index.getName(), index);
	}

	public GParameter findParameter(String name) {
		return this.h.findParameter(name);
	}

	public GVariable findVariable(String variableName) {
		return this.h.findVariable(variableName);
	}

	public GForAll newIntervalForAll(String indexName, int start, GParameter end) {
		return this.newIntervalForAll(indexName, new GIntValue(start), end);
	}
	
	public GForAll newIntervalForAll(String indexName, GExpr start, GParameter end) {
		GForAll forAll = new GIntervalForAll(indexName, start, end, this.h);
		this.forAlls.add(forAll);
		return forAll;
	}
	
	public GForAll newForAll(String indexName, GSet set) {
		GForAll forAll = new GSetForAll(indexName, set);
		this.forAlls.add(forAll);
		return forAll;
	}

	public void addVariable(GVariable variable) {
		this.h.addVariable(variable);
	}

	@JsonIgnore
	public GH getH() {
		return h;
	}
}
