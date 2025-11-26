package edu.uclm.alarcos.qmutator.annealing.g;

import java.util.ArrayList;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class GVariable {

	private String name;
	private ArbitraryArray<Double> array;

	public GVariable(String name) {
		this.name = name;
		this.array = new ArbitraryArray<>();
	}
	
	public void setValue(Double value, int... coords) {
		this.array.setValue(value, coords);
	}

	public void setValues(ArrayList<Double> values) {
		this.array.setValues(values);
	}

	public ArbitraryArray<Double> getValues() {
		return array;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		String r = name + " = { ";
		if (this.array!=null)
			r = r + array.toString();
		r = r + " } ";
		return r;
	}
	
	public JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", "GVariable");
		jso.put("name", this.name);
		if (this.array.getDimensions()!=null)
			jso.put("array", this.array.toJSON());
		return jso;
	}

	@JsonIgnore
	public Double getCardinal() {
		return array.getCardinal();
	}

	@JsonIgnore
	public int[] getDimensions() {
		return array.getDimensions();
	}

	public void setDimensions(int... dimensions) {
		this.array.setDimensions(dimensions);
	}

	public void setDimensions(ArrayList<Integer> dimensions) {
		this.array.setDimensions(dimensions);
	}

	public String toML() {
		return "<mi>" + this.name + "</mi>";
	}

}
