package edu.uclm.alarcos.qmutator.annealing.g.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestCombination {
	private long index;
	private TestParameter[] parameters;
	private int[] valuesIndexes;
	
	public TestCombination(int size) {
		this.parameters=new TestParameter[size];
		this.valuesIndexes=new int[size];
		for (int i=0; i<size; i++)
			this.valuesIndexes[i]=-1;
		
		this.index=0;
	}
	
	public long getIndex() {
		return index;
	}

	public int size() {
		return this.parameters.length;
	}

	public final Double[] getValues() {
		Double[] values=new Double[this.parameters.length];
		TestParameter parameter;
		int valueIndex;
		for (int i=0; i<parameters.length; i++) {
			parameter = parameters[i];
			valueIndex = valuesIndexes[i];
			values[i]=parameter.getValues().get(valueIndex);
		}
		return values;
	}

	public int getWeightOfPairs(PairsTable[] pptt) {
		int result=0;
		for (int i=0; i<pptt.length; i++) {
			PairsTable pt=pptt[i];
			result+=pt.weightOfThePairs(this);
		}
		return result;
	}

	public int getNumberOfPairsVisited(PairsTable[] pptt) {
		int result=0;
		for (int i=0; i<pptt.length; i++) {
			PairsTable pt=pptt[i];
			result+=pt.getNumberOfPairsVisited(this);
		}
		return result;
	}
	
	public List<Pair> getPairsVisited(PairsTable[] pptt) {
		List<Pair> result=new ArrayList<>();
		for (int i=0; i<pptt.length; i++) {
			PairsTable pt=pptt[i];
			result.add(pt.getPairVisited(this));
		}
		return result;
	}

	public void visitPairs(Map<String, PairsTable> pptt) {
		for (int i=0; i<this.parameters.length; i++) {
			for (int j=i+1; j<this.parameters.length; j++) {
				PairsTable pairsTable=pptt.get(i + "." + j);
				pairsTable.visit(this.valuesIndexes[i], this.valuesIndexes[j]);
			}
		}
	}

	public String toString() {
		StringBuilder sbResult=new StringBuilder("{");
		int valueIndex;
		for (int i=0; i<parameters.length; i++) {
			valueIndex=this.valuesIndexes[i];
			
			if (valueIndex==-1)
				sbResult.append("null");
			else
				sbResult.append(parameters[i].getValue(this.valuesIndexes[i]));
			sbResult.append(", ");
		}
		String result=sbResult.toString();
		if (result.endsWith(", "))
			result=result.substring(0, result.length()-2);
		result+="}";
		return result;
	}
	
	public Double getValue(int parameterIndex) {
		int valueIndex=parameterIndex;
		TestParameter parameter=this.parameters[parameterIndex];
		return parameter.getValue(valueIndex);
	}
	
	public void setParameter(int index, TestParameter parameter) {
		this.parameters[index] = parameter;
	}
	
	public void setSelectedParameterValue(int index, int selectedValueIndex) {
		this.valuesIndexes[index] = selectedValueIndex;
	}

	public TestParameter getParameter(int index) {
		return parameters[index];
	}
	
	public int getValueIndex(int index) {
		return this.valuesIndexes[index];
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
