package edu.uclm.alarcos.qmutator.annealing.g;

import java.util.List;

import org.json.JSONObject;

public class GSetForAll extends GForAll {

	private GSet set;
	
	GSetForAll(String variable, GSet set) {
		super(variable);
		this.set = set;
	}
	
	public GSetForAll(JSONObject jso) {
		super(jso.getString("variable"));
	}
	
	@Override
	public List<Integer> getValues() {
		return this.set.getValues();
	}
		
	@Override
	public String toString() {
		return "forAll " + this.variable + " in " + this.set.toString();
	}
	public JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", this.getClass().getSimpleName());
		jso.put("variable", this.variable);
		jso.put("set", this.set.toJSON());
		return jso;
	}
	
	public String toML() {
		StringBuilder sb = new StringBuilder();
		
		return sb.toString();
	}
}
