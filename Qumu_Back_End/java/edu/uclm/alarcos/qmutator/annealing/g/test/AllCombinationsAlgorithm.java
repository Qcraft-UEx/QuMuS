package edu.uclm.alarcos.qmutator.annealing.g.test;

import java.util.ArrayList;
import java.util.List;

public class AllCombinationsAlgorithm extends Algorithm {
	public AllCombinationsAlgorithm() {
		super();
	}

	@Override
	public List<TestCombination> buildCombinations() {
		int r=1;
		for (int i=0; i<this.parameters.size(); i++) {
			r=r*this.parameters.get(i).size();
		}
		
		List<TestCombination> result=new ArrayList<>();
		if (this.parameters.isEmpty())
			return result;
		int cont = 0;
		if (this.parameters.size()==1) {
			TestParameter parameter=this.parameters.get(0);
			for (int i=0; i<parameter.size(); i++) {
				TestCombination combination=new TestCombination(1);
				combination.setParameter(0, parameter);
				combination.setSelectedParameterValue(0, i);
				combination.setIndex(cont++);
				result.add(combination);
			}
			return result;
		}
		
		List<List<Integer>> lists=new ArrayList<>();
		lists=this.cartesianProduct2(this.parameters.get(0), this.parameters.get(1));
		for (int i=2; i<this.parameters.size(); i++) 
			lists=this.cartesianProduct(lists, this.parameters.get(i));
		
		for (int i=0; i<lists.size(); i++) {
			TestCombination combination=new TestCombination(this.parameters.size());
			List<Integer> sComb=lists.get(i);
			for (int j=0; j<sComb.size(); j++) {
				combination.setParameter(j, this.parameters.get(j));
				combination.setSelectedParameterValue(j, sComb.get(j));
			}
			combination.setIndex(cont++);
			result.add(combination);
		}
		return result;
	}

	private List<List<Integer>> cartesianProduct(List<List<Integer>> lists, TestParameter parameter) {
		List<List<Integer>> result=new ArrayList<>();
		for (List<Integer> list : lists) {
			for (int i=0; i<parameter.size(); i++) {
				List<Integer> newList = new ArrayList<>();
				for (Integer listValue : list)
					newList.add(listValue);
				newList.add(i);
				result.add(newList);
			}
		}
		return result;
	}

	private List<List<Integer>> cartesianProduct2(TestParameter parameterA, TestParameter parameterB) {
		List<List<Integer>> result=new ArrayList<>();
		for (int i=0; i<parameterA.size(); i++) {
			for (int j=0; j<parameterB.size(); j++) {
				List<Integer> list=new ArrayList<>();
				list.add(i); 
				list.add(j);
				result.add(list);
			}
		}
		return result;
	}

	@Override
	public String getName() {
		return "All combinations";
	}

	@Override
	public String getCredits() {
		return "Macario Polo";
	}
	
	@Override
	public boolean requiresRegister() {
		return true;
	}
}
