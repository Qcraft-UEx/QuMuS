package edu.uclm.alarcos.qmutator.annealing;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Combination {

	private List<XValue> data;
	private double value;
	private String html;
	
	public Combination() {
	}
	
	public Combination(String html) {
		this.data = new ArrayList<>();
		char c;
		XValue xValue;
		int index = 0;
		int i = 0;
		while (i<html.length()) {
			xValue = new XValue();
			xValue.setIndex(index++);

			c = html.charAt(i);
			if (c=='<') {
				i=i+3;
				c = html.charAt(i);
				xValue.setFixed(true);
				i = i + 5;
			} else {
				i = i + 1;
			}
			xValue.setValue(c=='0' ? 0 : 1);
			this.data.add(xValue);
		}
	}

	public Combination(JSONObject jso) {
		this.data = new ArrayList<>();
		this.value = jso.getDouble("value");
		JSONArray jsaData = jso.getJSONArray("data");
		XValue xValue;
		JSONObject jsoValue;
		for (int i=0; i<jsaData.length(); i++) {
			jsoValue = jsaData.getJSONObject(i);
			xValue = new XValue();
			xValue.setIndex(i);
			xValue.setFixed(jsoValue.getBoolean("fixed"));
			xValue.setValue(jsoValue.getInt("value"));
			this.data.add(xValue);
		}
	}

	public void setData(List<XValue> data) {
		this.data = data;
		StringBuilder sb = new StringBuilder();
		for (XValue xValue : data)
			sb.append(!xValue.isFixed() ? ("<b>" + xValue.getValue() + "</b>") : xValue.getValue());
		this.html = sb.toString();
	}

	public void setValue(double value) {
		this.value = value;
	}

	@JsonIgnore
	public List<XValue> getData() {
		return data;
	}
	
	public String getHtml() {
		return html;
	}
	
	public double getValue() {
		return value;
	}
	
	public JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("value", value);
		JSONArray jsaData = new JSONArray();
		for (XValue xValue : this.data)
			jsaData.put(xValue.toJSON());
		jso.put("data", jsaData);
		return jso;
	}
}
