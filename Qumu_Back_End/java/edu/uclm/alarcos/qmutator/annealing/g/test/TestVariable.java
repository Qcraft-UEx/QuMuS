package edu.uclm.alarcos.qmutator.annealing.g.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestVariable {

	private String name;
	private ArrayList<TestParameter> cardinals;
	private ArrayList<Double> testValues;

	public TestVariable(String name) {
		this.name = name;
		this.cardinals = new ArrayList<>();
		this.testValues = new ArrayList<>();
	}

	public void setCardinals(List<TestParameter> parameters, String cardinals) {
		String[] st = cardinals.split(",");
		String parameterName;
		TestParameter testParameter;
		for (int i=0; i<st.length; i++) {
			parameterName = st[i].trim();
			testParameter = findParameter(parameters, parameterName);
			if (testParameter==null) 
				continue;
			this.cardinals.add(testParameter);
		}
	}
	
	private TestParameter findParameter(List<TestParameter> parameters, String parameterName) {
		for (TestParameter testParameter : parameters)
			if (testParameter.getName().equals(parameterName))
				return testParameter;
		return null;
	}
	
	public String getName() {
		return name;
	}

	public ArrayList<Integer> getInstantiatedCardinals() {
		ArrayList<Integer> result = new ArrayList<>();
		for (TestParameter cardinal : this.cardinals)
			result.add((int) cardinal.getValue());
		return result;
	}

	public void setTestValues(String values) {
		String[] st = values.split(",");
		Double value;
		for (int i=0; i<st.length; i++) {
			value = Double.parseDouble(st[i].trim());
			this.testValues.add(value);
		}
	}

	public ArrayList<TestParameter> getCardinals() {
		return cardinals;
	}
	
	public ArrayList<Double> getTestValues() {
		return testValues;
	}
}
