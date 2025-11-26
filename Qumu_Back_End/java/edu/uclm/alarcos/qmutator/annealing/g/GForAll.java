package edu.uclm.alarcos.qmutator.annealing.g;

import java.util.List;

import org.json.JSONObject;

public abstract class GForAll {

	protected String variable;
	
	public GForAll(String variable) {
		this.variable = variable;
	}
	
	@Override
	public abstract String toString();
	
	public abstract JSONObject toJSON();

	public abstract String toML();
	
	public abstract List<Integer> getValues();

	protected final String getVariable() {
		return this.variable;
	}
}
