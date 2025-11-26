package edu.uclm.alarcos.qmutator.annealing.g.test;

import java.util.ArrayList;
import java.util.List;

public class RandomAlgorithm extends Algorithm {
	protected int numberOfDesiredCombinations;

	@Override
	public List<TestCombination> buildCombinations() {
		List<TestCombination> result=new ArrayList<>();
		int i=0;
		TestParameter parameter;
		java.util.Random dado=new java.util.Random();
		while (i<this.numberOfDesiredCombinations) {
			TestCombination combination=new TestCombination(this.parameters.size());
			for (int j=0; j<this.parameters.size(); j++) {
				parameter=this.parameters.get(j);
				int selected=dado.nextInt(parameter.size());				
				//combination.setValue(i, parameter.get(selected));
				combination.setParameter(j, parameter);
				combination.setSelectedParameterValue(j, selected);
			}
			result.add(combination);
			i++;
		}
		return result;
	}

	public int getNumberOfDesiredCombinations() {
		return this.numberOfDesiredCombinations;
	}

	public void setNumberOfDesiredCombinations(int numberOfDesiredCombinations) {
		this.numberOfDesiredCombinations = numberOfDesiredCombinations;
	}

	@Override
	public String getName() {
		return "Random";
	}
	
	@Override
	public String getCredits() {
		return "Implemented by Macario Polo and Beatriz Pérez";
	}
	
	@Override
	public boolean requiresRegister() {
		return true;
	}
}
