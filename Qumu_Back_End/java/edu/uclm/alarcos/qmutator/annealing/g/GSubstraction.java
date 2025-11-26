package edu.uclm.alarcos.qmutator.annealing.g;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.ResultExpr;
import edu.uclm.alarcos.qmutator.annealing.Substraction;

public class GSubstraction extends GExpr {

	private GRule rule;
	private GExpr left, right;
	
	public GSubstraction(GRule rule) {
		this.rule = rule;
	}

	public GSubstraction(GRule rule, JSONObject jso) {
		this(rule);
		GExpr left = GExpr.build(jso.getJSONObject("left"), rule.getH());
		GExpr right = GExpr.build(jso.getJSONObject("right"), rule.getH());
		this.left = left;
		this.right = right;
	}

	public void setLeft(GExpr left) {
		this.left = left;
	}
	
	@JsonIgnore
	public GExpr getLeft() {
		return left;
	}
	
	public void setRight(GExpr right) {
		this.right = right;
	}

	@JsonIgnore
	public GExpr getRight() {
		return right;
	}

	@Override

	@JsonIgnore
	public ResultExpr getResultExpr(CH f, String indexName, Integer index) {
		Substraction subs = new Substraction(f);
		subs.setLeft(this.left.getResultExpr(f, indexName, index));
		subs.setRight(this.right.getResultExpr(f, indexName, index));
		return subs;
	}

	@Override
	@JsonIgnore
	protected Double getValue() {
		Double result = this.left.getValue() - this.right.getValue();
		return result;
	}

	@Override
	protected GExpr copy() {
		GSubstraction result = new GSubstraction(this.rule);
		result.setLeft(this.left.copy());
		result.setRight(this.right.copy());
		return result;
	}

	@Override
	protected GExpr replace(String variable, int value) {
		this.left.replace(variable, value);
		this.right.replace(variable, value);
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.left.toString() + " - " + this.right.toString());
		return sb.toString();
	}
	
	@Override
	protected JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", this.getClass().getSimpleName());
		jso.put("left", this.left.toJSON());
		jso.put("right", this.right.toJSON());
		return jso;
	}

	@Override
	protected String toML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<mfenced separators='' open='(' close=')'>");
		sb.append(this.left.toML());
		sb.append("<mo>-</mo>");
		sb.append(this.right.toML());
		sb.append("</mfenced>");
		return sb.toString();
	}
}
