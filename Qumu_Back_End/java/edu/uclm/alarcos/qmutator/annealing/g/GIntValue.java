package edu.uclm.alarcos.qmutator.annealing.g;

import org.json.JSONObject;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.DoubleValue;
import edu.uclm.alarcos.qmutator.annealing.ResultExpr;

public class GIntValue extends GExpr {

	private int value;
	
	public GIntValue(JSONObject jso, GH h) {
		this.value = jso.getInt("value");
	}

	public GIntValue(int value) {
		this.value = value;
	}

	@Override
	public ResultExpr getResultExpr(CH f, String indexName, Integer index) {
		DoubleValue dv = new DoubleValue(f);
		dv.setValue(value);
		return dv;
	}

	@Override
	protected Double getValue() {
		return (double) this.value;
	}

	@Override
	protected GExpr copy() {
		return new GIntValue(value);
	}

	@Override
	protected GExpr replace(String variable, int value) {
		return this;
	}

	@Override
	public String toString() {
		return "" + this.value;
	}

	@Override
	protected JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", this.getClass().getSimpleName());
		jso.put("value", this.value);
		return jso;
	}

	@Override
	protected String toML() {
		return "<mn>" + this.value + "</mn>";
	}
}
