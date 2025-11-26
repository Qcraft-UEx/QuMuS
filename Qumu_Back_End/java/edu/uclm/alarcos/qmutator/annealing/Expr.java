package edu.uclm.alarcos.qmutator.annealing;

import org.json.JSONObject;

public abstract class Expr {
	
	public JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", this.getClass().getSimpleName());
		completeJSON(jso);
		return jso;
	}

	protected abstract void completeJSON(JSONObject jso);

}
