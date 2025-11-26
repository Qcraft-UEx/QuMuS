package edu.uclm.alarcos.qmutator.annealing.g;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.ResultExpr;
import edu.uclm.alarcos.qmutator.annealing.Sum;

public class GSum extends GExpr {

	private GRule rule;
	private ArrayList<GExpr> exprs;
	
	public GSum(GRule rule) {
		this.rule = rule;
		this.exprs = new ArrayList<>();
	}

	public GSum(GRule rule, JSONObject jso) {
		this(rule);
		GExpr left = GExpr.build(jso.getJSONObject("left"), rule.getH());
		GExpr right = GExpr.build(jso.getJSONObject("right"), rule.getH());
		this.exprs.add(left);
		this.exprs.add(right);
	}
	
	public void add(GExpr expr) {
		this.exprs.add(expr);
	}
	
	public void setExprs(ArrayList<GExpr> exprs) {
		this.exprs = exprs;
	}

	@Override
	@JsonIgnore
	public ResultExpr getResultExpr(CH f, String indexName, Integer index) {
		Sum sum = new Sum(f);
		for (GExpr expr : this.exprs)
			sum.add(expr.getResultExpr(f, indexName, index));
		return sum;
	}

	@Override
	@JsonIgnore
	protected Double getValue() {
		Double result = 0.0;
		for (GExpr expr : this.exprs)
			result += expr.getValue();
		return result;
	}

	@Override
	protected GExpr copy() {
		GSum result = new GSum(this.rule);
		for (GExpr expr : this.exprs)
			result.exprs.add(expr.copy());
		return result;
	}

	@Override
	protected GExpr replace(String variable, int value) {
		for (GExpr expr : this.exprs)
			expr.replace(variable, value);
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<this.exprs.size()-1; i++)
			sb.append(exprs.get(i).toString() + " + ");
		sb.append(exprs.get(this.exprs.size()-1).toString());
		return sb.toString();
	}
	
	@Override
	protected JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", this.getClass().getSimpleName());
		JSONArray jsa = new JSONArray();
		for (int i=0; i<this.exprs.size()-1; i++)
			jsa.put(this.exprs.get(i).toJSON());
		jso.put("exprs", jsa);
		return jso;
	}

	public int size() {
		return this.exprs.size();
	}

	public ArrayList<GExpr> getExprs() {
		return exprs;
	}
	
	@Override
	protected String toML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<mfenced separators='' open='(' close = ')'>");
		for (int i=0; i<exprs.size()-1; i++) { 
			sb.append(exprs.get(i).toML());
			sb.append("<mo>+</mo>");
		}
		sb.append(exprs.get(exprs.size()-1).toML());
		sb.append("</mfenced>");
		return sb.toString();
	}
}
