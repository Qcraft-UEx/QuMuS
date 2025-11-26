package edu.uclm.alarcos.qmutator.annealing;

import org.json.JSONObject;

public class XValue {

	private Integer value;
	private boolean fixed;
	private int index;

	public XValue() {
		this.value = null;
		this.fixed = false;
	}

	public XValue(JSONObject jso) {
		if (jso.has("value"))
			this.value = jso.getInt("value");
		this.fixed = jso.getBoolean("fixed");
		this.index = jso.getInt("index");
	}

	public Integer getValue() {
		return value;
	}
	
	public boolean isFixed() {
		return fixed;
	}

	public JSONObject toJSON() {
		return new JSONObject().put("value", this.value).put("fixed", fixed).put("index", index);
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setValue(int value) {
		this.value = value;
		this.fixed = true;
	}
	
	@Override
	public String toString() {
		return "(" + value + ", " + fixed + ")";
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}
}
