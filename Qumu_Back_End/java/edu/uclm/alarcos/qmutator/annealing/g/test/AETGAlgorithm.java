package edu.uclm.alarcos.qmutator.annealing.g.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AETGAlgorithm extends Algorithm {

	@Override
	public List<TestCombination> buildCombinations(){
		this.pairsTables=buildPairTables();

		TestCombination aux=null;
		int selectedPos=0;
		List<TestCombination> result=new ArrayList<>();
		TestParameter parameter;
		int numberOfParameters=this.parameters.size();
		while (getPairWithoutVisits()!=null) {
			int max;
			TestCombination combination = initializeNewCombination();
			for (int parameterIndex=0; parameterIndex<numberOfParameters; parameterIndex++) {
				if (combination.getValueIndex(parameterIndex)==-1) {
					parameter=this.parameters.get(parameterIndex);
					max=Integer.MIN_VALUE;
					for (int valueIndex=0; valueIndex<parameter.size(); valueIndex++) {
						aux=copy(combination);
						aux.setParameter(parameterIndex, parameter);
						aux.setSelectedParameterValue(parameterIndex, valueIndex);
						int pairsVisited=getPairsVisited(aux, parameterIndex);
						if (pairsVisited>max) {
							max=pairsVisited;
							selectedPos=valueIndex;
						}
					}
					aux.setParameter(parameterIndex, parameter);
					aux.setSelectedParameterValue(parameterIndex, selectedPos);
					combination.setParameter(parameterIndex, parameter);
					combination.setSelectedParameterValue(parameterIndex, selectedPos);
				}
			}
			combination.visitPairs(this.pairsTables);
			result.add(combination);
		}
		return result;
	}
		
	protected int getPairsVisited(TestCombination combination, int parameterIndex) {
		int pairsVisited=0;
		
		for (int i=0; i<combination.size(); i++) {
			if (combination.getValueIndex(i)!=-1 && i!=parameterIndex) {
				Pair pair=null;
				PairsTable pairsTable;
				if (i>parameterIndex) {
					pairsTable=this.findPairsTable(parameterIndex, i);
					pair=pairsTable.getPair(combination.getValueIndex(parameterIndex), combination.getValueIndex(i));
				} else {
					pairsTable=this.findPairsTable(i, parameterIndex);
					pair=pairsTable.getPair(combination.getValueIndex(i), combination.getValueIndex(parameterIndex));
				}
				if (pair==null)
					return Integer.MIN_VALUE;
				if (pair.getVisits()==0)
					pairsVisited++;
			}
			
		}
		return pairsVisited;
	}
	
	private TestCombination copy(TestCombination combination) {
		TestCombination result=new TestCombination(combination.size());
		for (int i=0; i<combination.size(); i++) {
			result.setParameter(i, combination.getParameter(i));
			result.setSelectedParameterValue(i, combination.getValueIndex(i));
		}
		return result;
	}

	private TestCombination initializeNewCombination() {
		TestCombination selected=new TestCombination(this.parameters.size());
		int max=Integer.MIN_VALUE;
		int selectedParameterIndex=0, selectedValueIndex=-1, numberOfValues;
		TestParameter parameter=null;
		for (int parameterIndex=0; parameterIndex<this.parameters.size(); parameterIndex++) {
			parameter=this.parameters.get(parameterIndex);
			numberOfValues=parameter.size();
			for (int valueIndex=0; valueIndex<numberOfValues; valueIndex++) {
				int pairsVisited=getPairsVisited(parameterIndex, valueIndex);
				if (pairsVisited>max) {
					selectedParameterIndex=parameterIndex;
					selectedValueIndex=valueIndex;
					max=pairsVisited;
				}
			}
		}
		TestParameter selectedParameter=this.parameters.get(selectedParameterIndex);
		selected.setParameter(selectedParameterIndex, selectedParameter);
		selected.setSelectedParameterValue(selectedParameterIndex, selectedValueIndex);
		return selected;
	}

	private Pair getPairWithoutVisits() {
		Collection<PairsTable> pptt = this.pairsTables.values();
		for (PairsTable pt : pptt) {		
			Pair pair=pt.getPairWithWeight(0);
			if (pair!=null) {
				return pair;
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return "aetg";
	}

	@Override
	public String getCredits() {
		return "Macario Polo and Beatriz Pérez";
	}

	@Override
	public boolean requiresRegister() {
		return true;
	}
}