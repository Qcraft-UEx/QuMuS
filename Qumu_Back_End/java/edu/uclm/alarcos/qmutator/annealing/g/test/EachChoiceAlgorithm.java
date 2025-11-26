package edu.uclm.alarcos.qmutator.annealing.g.test;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EachChoiceAlgorithm extends Algorithm {

	@Override
	public List<TestCombination> buildCombinations() {
		List<List<Boolean>> visitados=new ArrayList<>();
		for (TestParameter parameter : this.parameters) {
			//randomize(parameter);
			List<Boolean> vb=new ArrayList<>();
			for (int i=0; i<parameter.size(); i++)
				vb.add(false);
			visitados.add(vb);
		}
		
		List<TestCombination> result=new ArrayList<>();
		Random rnd=new SecureRandom();
		TestParameter parameter;
		while (thereAreUnvisitedElements(visitados)) {
			TestCombination combination=new TestCombination(this.parameters.size());
			for (int i=0; i<this.parameters.size(); i++) {
				parameter=this.parameters.get(i);
				int selected=-1;
				List<Boolean> vb=visitados.get(i);
				for (int j=0; j<vb.size() && selected==-1; j++) {
					if (!vb.get(j)) {
						selected=j;
						vb.set(j, true);
					}
				}
				if (selected==-1)
					selected=rnd.nextInt(vb.size());
				combination.setParameter(i, parameter);
				combination.setSelectedParameterValue(i, selected);
			}
			result.add(combination);
		}
		return result;
	}

	private void randomize(TestParameter parameter) {
		Random rnd=new SecureRandom();
		for (int i=0; i<parameter.size()/2; i++) {
			int x=rnd.nextInt(parameter.size());
			int y=rnd.nextInt(parameter.size());
			Double eX=parameter.getValue(x);
			Double eY=parameter.getValue(y);
			Double auxi=eX;
			parameter.setValue(x, eY);
			parameter.setValue(y, auxi);
		}
	}

	private boolean thereAreUnvisitedElements(List<List<Boolean>> visitados) {
		for (List<Boolean> vb : visitados)
			for (boolean b : vb)
				if (!b)
					return true;
		return false;
	}

	@Override
	public String getName() {
		return "each choice";
	}

	@Override
	public String getCredits() {
		return "Macario Polo and Beatriz Pérez";
	}
	
	@Override
	public boolean requiresRegister() {
		return false;
	}
}