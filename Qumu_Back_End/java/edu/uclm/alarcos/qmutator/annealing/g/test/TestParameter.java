package edu.uclm.alarcos.qmutator.annealing.g.test;

import java.util.ArrayList;

public class TestParameter {

	private String name;
	private int currentIndex;
	private ArrayList<Double> values = new ArrayList<>();
	private double currentValue;

	public TestParameter(String name) {
		this.name = name;
		this.currentIndex = 0;
	}

	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.name + "={");
		for (Double value : this.values)
			sb.append(value.toString() + ", ");
		String r = sb.toString();
		r = r.substring(0, r.length()-2);
		r = r + "}";
		return r;
	}

	public void setValues(String values) {
		String[] st = values.split(",");
		for (int i=0; i<st.length; i++) {
			Double value = Double.parseDouble(st[i].trim());
			this.values.add(value);
		}
	}

	public double getValue() {
		return this.values.get(this.currentIndex);
	}

	public int size() {
		return this.values.size();
	}

	public Double getValue(int index) {
		return this.values.get(index);
	}

	public ArrayList<Double> getValues() {
		return this.values;
	}

	public void setValue(int index, Double value) {
		this.values.set(index, value);
	}

	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}

	public double getCurrentValue() {
		return currentValue;
	}
}
