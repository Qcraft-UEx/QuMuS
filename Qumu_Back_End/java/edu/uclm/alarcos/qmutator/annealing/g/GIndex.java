package edu.uclm.alarcos.qmutator.annealing.g;

import org.json.JSONObject;

public class GIndex {

	private String name;
	private GExpr start;
	
	public GIndex(String name, int value) {
		this.name = name;
		this.start = new GIntValue(value);
	}

	public GIndex(String name, GExpr start) {
		this.name = name;
		this.start = start;
	}

	public void setStart(GExpr start) {
		this.start = start;
	}

	public Integer getValue(int index) {
		return (int) (start.getValue() + index);
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return this.name + "=" + this.start.toString();
	}

	public JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", this.getClass().getSimpleName());
		jso.put("name", name);
		jso.put("start", this.start.toJSON());
		return jso;
	}
	
	public GExpr getStart() {
		return start;
	}

	public String toML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<mrow>");
		sb.append("<mi>" + this.name + "</mi>");
		sb.append("<mo>=</mo>");
		sb.append(this.start.toML());
		sb.append("</mrow>");
		return sb.toString();
	}
}
