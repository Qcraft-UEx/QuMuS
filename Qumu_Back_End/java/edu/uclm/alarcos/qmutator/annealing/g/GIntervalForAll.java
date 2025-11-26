package edu.uclm.alarcos.qmutator.annealing.g;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class GIntervalForAll extends GForAll {

	private GExpr start;
	private GExpr end;
	
	GIntervalForAll(String variable, GExpr start, GExpr end, GH h) {
		super(variable);
		this.start = start;
		this.end = end;
	}
	
	public GIntervalForAll(JSONObject jso, GH h) {
		super(jso.getString("variable"));
		
		JSONObject jsoStart = jso.optJSONObject("start");
		if (jsoStart!=null)
			this.start = GExpr.build(jsoStart, h);
		else
			this.start = new GIntValue(jso.getInt("start"));
		
		JSONObject jsoEnd = jso.optJSONObject("end");
		if (jsoEnd!=null)
			this.end = GExpr.build(jsoEnd, h);
		else
			this.end = new GIntValue(jso.getInt("end"));
	}
	
	@Override
	public List<Integer> getValues() {
		List<Integer> values = new ArrayList<>();
		int start = this.getStart();
		int end = this.getEnd();
		for (int i=start; i<end; i++)
			values.add(new Integer(i));
		return values;
	}
	
	private int getStart() {
		return start.getValue().intValue();
	}
	
	private int getEnd() {
		return end.getValue().intValue();
	}
	
	@Override
	public String toString() {
		return "forAll " + variable + " in [" + start.toString() + ", " + this.end.toString() + ")";
	}
	public JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", this.getClass().getSimpleName());
		jso.put("variable", this.variable);
		jso.put("start", this.start.toJSON());
		jso.put("end", this.end.toJSON());
		return jso;
	}

	public void setStart(GExpr start) {
		this.start = start;
	}

	public void setEnd(GExpr end) {
		this.end = end;
	}

	public String toML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<mo>∀</mo>");
		sb.append("<mi>" + this.variable + "</mi>");
		sb.append("<mo>∈</mo>");
		sb.append("<mfenced separators='' open='[' close=')'>");
		sb.append(start.toML());
		sb.append("<mo>,</mo>");
		sb.append(end.toML());
		sb.append("</mfenced>");
		return sb.toString();
	}
}
