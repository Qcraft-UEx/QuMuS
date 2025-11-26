package edu.uclm.alarcos.qmutator.annealing;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Product extends ResultExpr {
	
	private ArrayList<ResultExpr> expressions = new ArrayList<>();
	
	public Product(CH f, JSONObject jso) {
		super(f);
		JSONArray jsa = jso.getJSONArray("expressions");
		for (int i=0; i<jsa.length(); i++) {
			JSONObject jsoExpr = jsa.getJSONObject(i);
			ResultExpr expr = ResultExpr.build(f, jsoExpr);
			this.expressions.add(expr);
			expr.setParent(this);
		}
	}
	
	public Product(CH f, ResultExpr... exprs) {
		super(f);
		for (ResultExpr expr : exprs) {
			this.expressions.add(expr);
			expr.setParent(this);
		}
	}
	
	public Product(CH f, List<ResultExpr> exprs) {
		super(f);
		for (ResultExpr expr : exprs) {
			this.expressions.add(expr);
			expr.setParent(this);
		}
	}
	
	public Product add(ResultExpr expr) {
		this.expressions.add(expr);
		expr.setParent(this);
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
		double r = 1;
		ResultExpr expr;
		for (int i=0; i<this.expressions.size(); i++) {
			expr = this.expressions.get(i);
			r = r * expr.getValue();
			if (r==0.0)
				break;
		}
		return r;
	}
	
	@Override
	public double getValue(List<XValue> comb, int... prods) {
		double r = 1;
		ResultExpr expr;
		for (int i=0; i<this.expressions.size(); i++) {
			expr = this.expressions.get(i);
			r = r * expr.getValue(comb, prods);
			if (r==0.0)
				break;
		}
		return r;
	}
	
	@Override
	public Lambda getLambda() {
		ResultExpr last = this.expressions.get(this.expressions.size()-1);
		if (last instanceof Lambda)
			return (Lambda) last;
		return null;
	}
	
	@Override
	public ResultExpr setLambda(double lambdaValue) {
		ResultExpr last = this.expressions.get(this.expressions.size()-1);
		if (last instanceof Lambda) {
			Lambda lambda = (Lambda) last;
			lambda.setValue(lambdaValue);
		}
		return last;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (ResultExpr expr : this.expressions)
			sb.append(expr.toString()).append("*");
		String r = sb.toString();
		if (r.endsWith("*"))
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
		for (int i=0; i<expressions.size()-1; i++) { 
			sb.append(expressions.get(i).toML());
			sb.append("<mo>·</mo>");
		}
		sb.append(expressions.get(expressions.size()-1).toML());
		return sb.toString();
	}
}
