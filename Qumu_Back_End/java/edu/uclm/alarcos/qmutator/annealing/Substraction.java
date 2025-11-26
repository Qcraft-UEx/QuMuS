package edu.uclm.alarcos.qmutator.annealing;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class Substraction extends ResultExpr {
	
	private ResultExpr left, right;
	
	public Substraction(CH f) {
		super(f);
	}
	
	public Substraction(CH f, JSONObject jso) {
		super(f);
		JSONObject left = jso.getJSONObject("left");
		JSONObject right = jso.getJSONObject("right");
		this.left = ResultExpr.build(f, left);
		this.right = ResultExpr.build(f, right);
	}
	
	public Substraction setLeft(ResultExpr left) {
		this.left = left;
		this.left.setParent(this);
		return this;
	}
	
	public Substraction setRight(ResultExpr right) {
		this.right = right;
		this.right.setParent(this);
		return this;
	}

	@Override
	protected void completeJSON(JSONObject jso) {
		jso.put("left", this.left.toJSON());
		jso.put("right", this.right.toJSON());
	}

	@Override
	public double getValue() {
		return left.getValue() - right.getValue();
	}
	
	@Override
	public double getValue(List<XValue> comb, int... prods) {
		return left.getValue(comb, prods) - right.getValue(comb, prods);
	}

	@Override
	public String toString() {
		return this.left.toString() + " - " + this.right.toString();
	}

	@Override
	public List<ResultExpr> getChildren() {
		List<ResultExpr> children = new ArrayList<>();
		children.add(this.left);
		children.add(this.right);
		return children;
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
