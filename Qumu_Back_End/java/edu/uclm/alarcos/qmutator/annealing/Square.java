package edu.uclm.alarcos.qmutator.annealing;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class Square extends ResultExpr {
	
	private ResultExpr expr;
	
	public Square(CH f) {
		super(f);
	}
	
	public Square(CH f, ResultExpr expr) {
		super(f);
		this.expr = expr;
	}
	
	public Square(CH f, JSONObject jso) {
		super(f);
		this.expr = ResultExpr.build(f, jso.getJSONObject("expr"));
		this.expr.setParent(this);
	}
	
	public Square setExpr(ResultExpr expr) {
		this.expr = expr;
		this.expr.setParent(this);
		return this;
	}

	@Override
	protected void completeJSON(JSONObject jso) {
		jso.put("expr", expr.toJSON());
	}

	@Override
	public double getValue() {
		double value = expr.getValue();
		return value * value;
	}
	
	@Override
	public double getValue(List<XValue> comb, int... prods) {
		double value = expr.getValue(comb, prods);
		return value * value;
	}

	@Override
	public String toString() {
		return "(" + this.expr.toString() + ")^2";
	}

	@Override
	public List<ResultExpr> getChildren() {
		List<ResultExpr> children = new ArrayList<>();
		children.add(expr);
		return children;
	}

	@Override
	protected String toML() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<msup>");
		sb.append(this.expr.toML());
		sb.append("<mn>2</mn>");
		sb.append("</msup>");
		return sb.toString();
	}
}
