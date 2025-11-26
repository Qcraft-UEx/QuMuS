package edu.uclm.alarcos.qmutator.annealing.g;

import org.json.JSONObject;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.ResultExpr;

public class GCardinal extends GExpr {

	private GH h;
	private GVariable variable;

	public GCardinal(GH h) {
		this.h = h;
	}

	public GCardinal(GH h, JSONObject jso) {
		this(h);
		
		String variableName = jso.getString("variable");
		if (!variableName.equals("x"))
			h.putVariable(variableName);
	}

	@Override
	public ResultExpr getResultExpr(CH f, String indexName, Integer index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Double getValue() {
		if (this.variable.getName().equals("x"))
			return (double) h.getXCardinal();
		GVariable variable = h.findVariable(this.variable.getName());
		return variable.getCardinal();
	}

	@Override
	protected GExpr copy() {
		GCardinal result = new GCardinal(this.h);
		result.variable = this.variable;
		return result;
	}

	@Override
	protected GExpr replace(String variable, int value) {
		return this;
	}

	@Override
	public String toString() {
		return "|" + this.variable + "|";
	}

	@Override
	protected JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", "GCardinal");
		jso.put("variable", this.variable.toJSON());
		return jso;
	}

	@Override
	protected String toML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<mfenced separators=''  open='|' close='|'>");
		sb.append(this.variable.toML());
		sb.append("</mfenced>");
		return sb.toString();
	}
}
