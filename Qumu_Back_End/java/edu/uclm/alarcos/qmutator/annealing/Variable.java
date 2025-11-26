package edu.uclm.alarcos.qmutator.annealing;

import org.json.JSONObject;

public class Variable extends Expr {
	private String name;
	
	public Variable(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Variable setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	protected void completeJSON(JSONObject jso) {
		jso.put("name", name);
	}

	@Override
	public String toString() {
		return name;
	}
}
