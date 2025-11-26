package edu.uclm.alarcos.qmutator.annealing;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Sum extends ResultExpr {
	
	private ArrayList<ResultExpr> expressions = new ArrayList<>();
	
	public Sum(CH f) {
		super(f);
	}
	
	public Sum(CH f, JSONObject jso) {
		this(f);
		JSONArray jsa = jso.getJSONArray("expressions");
		for (int i=0; i<jsa.length(); i++) {
			JSONObject jsoExpr = jsa.getJSONObject(i);
			ResultExpr expr = ResultExpr.build(f, jsoExpr);
			this.expressions.add(expr);
			expr.setParent(this);
		}
	}
	
	public Sum(CH f, ResultExpr... exprs) {
		this(f);
		for (ResultExpr expr : exprs) {
			this.expressions.add(expr);
			expr.setParent(this);
		}
	}
	
	public Sum(CH f, List<ResultExpr> exprs) {
		this(f);
		for (ResultExpr expr : exprs) {
			this.expressions.add(expr);
			expr.setParent(this);
		}
	}
	
	public Sum add(ResultExpr expr) {
		this.expressions.add(expr);
		expr.setParent(this);
		return this;
	}
	
	public Sum add(int value) {
		DoubleValue doubleValue = new DoubleValue(this.function, value);
		this.expressions.add(doubleValue);
		doubleValue.setParent(this);
		return this;
	}
	
	public Sum add(String expr) throws Exception {
		int posBarra = expr.indexOf('_');
		if (posBarra!=-1) {
			String variableName = expr.substring(0, posBarra);
			X x = this.function.getX();
			if (!variableName.equals(x.getName())) {
				IndexedVariableValue ivv = new IndexedVariableValue(this.function, expr);
				this.expressions.add(ivv);
				ivv.setParent(this);
			} else {
				UseOfX useOfX = new UseOfX(this.function, expr);
				this.expressions.add(useOfX);
				useOfX.setParent(this);
			}
		}
		return this;
	}

	@Override
	protected void completeJSON(JSONObject jso) {
		JSONArray jsa = new JSONArray();
		for (Expr expr : this.expressions)
			jsa.put(expr.toJSON());
		jso.put("expressions", jsa);
	}

	@Override
	public double getValue() {
		double r = 0;
		for (ResultExpr expr : this.expressions)
			r = r + expr.getValue();
		return r;
	}
	
	@Override
	public double getValue(List<XValue> comb, int... prods) {
		double r = 0;
		for (ResultExpr expr : this.expressions)
			r = r + expr.getValue(comb, prods);
		return r;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (ResultExpr expr : this.expressions)
			sb.append(expr.toString()).append("+");
		String r = sb.toString();
		if (r.endsWith("+"))
			r = r.substring(0, r.length()-1);
		if (!this.expressions.isEmpty()) {
			r = "(" + r + ")";
		}
		return r;
	}

	@Override
	public List<ResultExpr> getChildren() {
		return this.expressions;
	}
	
	@Override
	protected String toML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<mfenced separators='' open='(' close = ')'>");
		for (int i=0; i<expressions.size()-1; i++) { 
			sb.append(expressions.get(i).toML());
			sb.append("<mo>+</mo>");
		}
		sb.append(expressions.get(expressions.size()-1).toML());
		sb.append("</mfenced>");
		return sb.toString();
	}
}
