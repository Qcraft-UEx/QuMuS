package edu.uclm.alarcos.qmutator.annealing;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

public class XValueList {
	
	private List<XValue> values;
	
	public XValueList() {
		this.values = new ArrayList<>();
	}

	public void add(XValue comb) {
		this.values.add(comb);
	}

	public int size() {
		return this.values.size();
	}

	public XValue get(int index) {
		return this.values.get(index);
	}
	
	public void set(int index, XValue value) {
		this.values.set(index, value);
	}

	public void set(int index, int value) {
		XValue xValue = this.values.get(index); 
		xValue.setValue(value);
	}

	public JSONArray toJSON() {
		JSONArray jsa = new JSONArray();
		XValue value;
		for (int i=0; i<this.values.size(); i++) {
			value = this.values.get(i);
			jsa.put(value.toJSON());
		}
		return jsa;
	}

}
