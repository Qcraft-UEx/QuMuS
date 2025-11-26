package edu.uclm.alarcos.qmutator.annealing;

import java.util.List;

import org.json.JSONObject;

public class Lambda extends ResultExpr {

	private double value;
	
	public Lambda(CH f) {
		super(f);
	}
	
	public Lambda(CH f, JSONObject jso) {
		super(f);
		this.value = jso.getInt("value");
	}

	public Lambda(CH f, double value) {
		super(f);
		this.value = value;
	}
	
	public Lambda setValue(double value) {
		this.value = value;
		return this;
	}

	@Override
	protected void completeJSON(JSONObject jso) {
		jso.put("value", value);
	}

	@Override
	public double getValue() {
		return this.value;
	}

	@Override
	public double getValue(List<XValue> comb, int... prods) {
		return this.value;
	}
	
	@Override
	public String toString() {
		return "" + this.getValue();
	}

	@Override
	public List<ResultExpr> getChildren() {
		return null;
	}

	@Override
	protected String toML() {
		return "<mn>" + this.value + "</mn>";
	}
}
